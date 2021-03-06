package io.deeplay.qchess.server.handlers;

import static io.deeplay.qchess.server.exceptions.ServerErrorCode.ERROR_CREATE_CLIENT_HANDLER;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.ERROR_GET_SOCKET_INPUT;
import static io.deeplay.qchess.server.exceptions.ServerErrorCode.ERROR_GET_SOCKET_OUTPUT;

import io.deeplay.qchess.server.exceptions.ServerException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Обслуживает клиента */
public class ClientHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Consumer<Integer> removeClientFromClientList;
    private final int id;
    private volatile boolean stop;

    public ClientHandler(
            final Socket socket, final Consumer<Integer> removeClientFromClientList, final int id)
            throws ServerException {
        try {
            this.socket = socket;
            this.removeClientFromClientList = removeClientFromClientList;
            this.id = id;
            final InputStream socketInput = tryGetSocketInput(socket);
            final OutputStream socketOutput = tryGetSocketOutput(socket);
            in = new BufferedReader(new InputStreamReader(socketInput, StandardCharsets.UTF_8));
            out =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(socketOutput, StandardCharsets.UTF_8)),
                            false);
        } catch (final ServerException e) {
            logger.warn("Ошибка создания обработчика для клиента {}: {}", this, e.getMessage());
            closeClient();
            throw new ServerException(ERROR_CREATE_CLIENT_HANDLER, e);
        }
    }

    private InputStream tryGetSocketInput(final Socket socket) throws ServerException {
        try {
            return socket.getInputStream();
        } catch (final IOException e) {
            logger.warn("Ошибка получения потока ввода сокета");
            throw new ServerException(ERROR_GET_SOCKET_INPUT, e);
        }
    }

    private OutputStream tryGetSocketOutput(final Socket socket) throws ServerException {
        try {
            return socket.getOutputStream();
        } catch (final IOException e) {
            logger.warn("Ошибка получения потока вывода сокета");
            throw new ServerException(ERROR_GET_SOCKET_OUTPUT, e);
        }
    }

    public void terminate() {
        stop = true;
    }

    @Override
    public void run() {
        logger.info("Присоединился новый клиент {}", this);
        try {
            while (!stop) {
                clientHandlerUpdate();
            }
        } catch (final IOException e) {
            logger.warn("Клиент {} разорвал подключение: {}", this, e.getMessage());
        } finally {
            closeClient();
            logger.debug("Клиент {} удален", this);
        }
    }

    private void clientHandlerUpdate() throws IOException {
        if (in.ready()) {
            final String request = in.readLine();
            final String response = ClientRequestHandler.process(request, id);
            sendIfNotNull(response);
        }
    }

    private void closeClient() {
        logger.debug("Закрытие обработчика для клиента {}...", this);
        if (out != null) {
            out.close();
            if (out.checkError()) logger.warn("Ошибка в потоке вывода клиенту в обработчике");
        }
        try {
            if (in != null) in.close();
        } catch (final IOException e) {
            logger.warn("Ошибка закрытия потока ввода от клиента в обработчике");
        }
        try {
            if (socket != null) socket.close();
        } catch (final IOException e) {
            logger.warn("Ошибка закрытия сокета клиента в обработчике");
        }
        removeClientFromClientList.accept(id);
        logger.debug("Обработчик клиента {} завершил свою работу", this);
    }

    /** Отправляет клиенту строку, если она не null */
    public void sendIfNotNull(final String json) {
        if (json != null) {
            out.println(json);
            out.flush();
        }
    }
}
