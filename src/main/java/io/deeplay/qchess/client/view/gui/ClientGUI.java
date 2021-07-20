package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.service.GameGUIAdapterService;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.model.ViewBoard;

public class ClientGUI implements IClientView {

    private ConnectFrame connectFrame;

    public ClientGUI() {}

    @Override
    public void startView() {
        ClientController.setView(this);
        // ConnectFrame connectFrame = new ConnectFrame();
        //connectFrame = new ConnectFrame();
        GameGUIAdapterService.init();
        Table tableWhite = new Table("onestyle", true);
        Table tableBlack = new Table("onestyle", false);
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void drawBoard() {
        connectFrame.getTable().repaint();
    }

    @Override
    public ViewBoard getBoard() {
        return null;
        //        throw new UnsupportedOperationException("ВАСЯ СДЕЛАЙ НОРМАЛЬНО");
    }

    @Override
    public void close() {}
}
