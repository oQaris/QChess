package io.deeplay.qchess.client.controller;

import io.deeplay.qchess.client.Client;
import io.deeplay.qchess.client.IClient;
import io.deeplay.qchess.client.exceptions.ClientException;
import io.deeplay.qchess.client.view.IClientView;
import java.util.Optional;

public class ClientController implements IClient {
    private final IClient client;
    private IClientView view;

    public ClientController(IClientView view) {
        this.view = view;
        client = new Client(this::getView);
    }

    /** @return окружение клиента */
    public Optional<IClientView> getView() {
        return Optional.of(view);
    }

    /**
     * Устанавливает окружение клиента
     *
     * <p>Необходимо использовать рабочее окружение перед использованием клиента, иначе клиент может
     * работать некорректно либо вообще не работать. Если окружение равно null, оно будет отключено
     * и не будет использоваться клиентом. По умолчанию окружение стоит null
     *
     * @param view окружение клиента
     */
    public void setView(IClientView view) {
        this.view = view;
    }

    @Override
    public void connect(String ip, int port) throws ClientException {
        client.connect(ip, port);
    }

    @Override
    public void disconnect() throws ClientException {
        client.disconnect();
    }

    @Override
    public boolean isConnected() {
        return client.isConnected();
    }

    @Override
    public int getPort() {
        return client.getPort();
    }

    @Override
    public void setPort(int port) throws ClientException {
        client.setPort(port);
    }

    @Override
    public String getIp() {
        return client.getIp();
    }

    @Override
    public void setIp(String ip) throws ClientException {
        client.setIp(ip);
    }

    public void sendMessageAll(String message) {
        client.sendMessageAll(message);
    }
}
