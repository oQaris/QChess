package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import io.deeplay.qchess.client.view.IClientView;

public class ClientGUI implements IClientView {

    public ClientGUI() {}

    @Override
    public void startView() {
        ClientController.setView(this);
        // ConnectFrame connectFrame = new ConnectFrame();
        ConnectFrame connectFrame = new ConnectFrame();
        //GameGUIAdapterService.init();
        //Table tableWhite = new Table("twostyle", true);
        //Table tableBlack = new Table("onestyle", false);
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void close() {}
}
