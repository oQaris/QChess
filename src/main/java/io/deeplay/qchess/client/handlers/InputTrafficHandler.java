package io.deeplay.qchess.client.handlers;

import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_CREATE_TRAFFIC_HANDLER;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_GET_SOCKET_INPUT;
import static io.deeplay.qchess.client.exceptions.ClientErrorCode.ERROR_GET_SOCKET_OUTPUT;

import io.deeplay.qchess.client.exceptions.ClientException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputTrafficHandler extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(InputTrafficHandler.class);
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private volatile boolean stop;

    public InputTrafficHandler(Socket socket) throws ClientException {
        try {
            this.socket = socket;
            InputStream socketInput = tryGetSocketInput(socket);
            OutputStream socketOutput = tryGetSocketOutput(socket);
            in = new BufferedReader(new InputStreamReader(socketInput, StandardCharsets.UTF_8));
            out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(socketOutput, StandardCharsets.UTF_8)),
                            false);
        } catch (ClientException e) {
            logger.warn("Ошибка создания обработчика трафика для клиента {}", this);
            closeHandler();
            throw new ClientException(ERROR_CREATE_TRAFFIC_HANDLER, e);
        }
    }

    private InputStream tryGetSocketInput(Socket socket) throws ClientException {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            logger.warn("Ошибка получения потока ввода сокета для клиента {}", this);
            throw new ClientException(ERROR_GET_SOCKET_INPUT, e);
        }
    }

    private OutputStream tryGetSocketOutput(Socket socket) throws ClientException {
        try {
            return socket.getOutputStream();
        } catch (IOException e) {
            logger.warn("Ошибка получения потока вывода сокета для клиента {}", this);
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
        } catch (Exception e) {
            logger.warn("Сервер разорвал подключение с обработчиком трафика {}", this);
        } finally {
            closeHandler();
        }
    }

    private void inputTrafficHandlerUpdate() throws IOException {
        if (in.ready()) {
            String request = in.readLine();
            String response = TrafficRequestHandler.process(request);
            send(response);
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
            logger.warn("Ошибка закрытия потока ввода в обработчике трафика {}", this);
        }
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            logger.warn("Ошибка закрытия сокета клиента в обработчике трафика {}", this);
        }
        logger.debug("Обработчик трафика {} завершил свою работу", this);
    }

    /** Отправляет серверу строку, никак не обрабатывая */
    public void send(String json) {
        if (json != null) {
            out.println(json);
            out.flush();
        }
    }
}
