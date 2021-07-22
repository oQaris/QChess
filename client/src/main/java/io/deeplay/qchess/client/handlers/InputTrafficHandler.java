package io.deeplay.qchess.client.handlers;

import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_CREATE_TRAFFIC_HANDLER;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_GET_SOCKET_INPUT;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_GET_SOCKET_OUTPUT;

import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.clientserverconversation.dto.main.ServerToClientDTO;
import io.deeplay.qchess.clientserverconversation.service.SerializationException;
import io.deeplay.qchess.clientserverconversation.service.SerializationService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputTrafficHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(InputTrafficHandler.class);
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Consumer<ServerToClientDTO> setLastResponse;
    private final Supplier<Boolean> waitForResponse;
    private volatile boolean stop;

    public InputTrafficHandler(
            Socket socket,
            Consumer<ServerToClientDTO> setLastResponse,
            Supplier<Boolean> waitForResponse)
            throws ClientException {
        try {
            this.socket = socket;
            this.setLastResponse = setLastResponse;
            this.waitForResponse = waitForResponse;
            InputStream socketInput = tryGetSocketInput(socket);
            OutputStream socketOutput = tryGetSocketOutput(socket);
            in = new BufferedReader(new InputStreamReader(socketInput, StandardCharsets.UTF_8));
            out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(socketOutput, StandardCharsets.UTF_8)),
                            false);
        } catch (ClientException e) {
            logger.warn("Ошибка создания обработчика трафика для клиента: {}", e.getMessage());
            closeHandler();
            throw new ClientException(ERROR_CREATE_TRAFFIC_HANDLER, e);
        }
    }

    private InputStream tryGetSocketInput(Socket socket) throws ClientException {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            logger.warn("Ошибка получения потока ввода сокета для клиента: {}", e.getMessage());
            throw new ClientException(ERROR_GET_SOCKET_INPUT, e);
        }
    }

    private OutputStream tryGetSocketOutput(Socket socket) throws ClientException {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            logger.warn("Ошибка получения потока вывода сокета для клиента: {}", e.getMessage());
            throw new ClientException(ERROR_GET_SOCKET_OUTPUT, e);
        }
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public void run() {
        try {
            while (!stop) {
                inputTrafficHandlerUpdate();
            }
        } catch (IOException e) {
            logger.warn("Сервер разорвал подключение с обработчиком трафика: {}", e.getMessage());
        } finally {
            closeHandler();
        }
    }

    private void inputTrafficHandlerUpdate() throws IOException {
        if (in.ready()) {
            String request = in.readLine();
            String response = TrafficRequestHandler.process(request);
            if (waitForResponse.get()) {
                try {
                    setLastResponse.accept(SerializationService.serverToClientDTOMain(request));
                } catch (SerializationException ignore) {
                    // Уже проверили в обработчике
                }
            }
            sendIfNotNull(response);
        }
    }

    private void closeHandler() {
        logger.debug("Закрытие обработчика трафика {}...", this);
        if (out != null) {
            out.close();
            if (out.checkError())
                logger.warn("Ошибка в потоке вывода обработчика трафика {}", this);
        }
        try {
            if (in != null) in.close();
        } catch (IOException e) {
            logger.warn("Ошибка закрытия потока ввода в обработчике трафика: {}", e.getMessage());
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.warn("Ошибка закрытия сокета клиента в обработчике трафика: {}", e.getMessage());
        }
        logger.debug("Обработчик трафика {} завершил свою работу", this);
    }

    /** Отправляет серверу строку, если она не null */
    public void sendIfNotNull(String json) {
        if (json != null) {
            out.println(json);
            out.flush();
        }
    }
}
