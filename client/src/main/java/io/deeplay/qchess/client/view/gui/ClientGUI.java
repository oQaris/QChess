package io.deeplay.qchess.client.view.gui;

import io.deeplay.qchess.client.controller.ClientController;
import io.deeplay.qchess.client.view.IClientView;
import io.deeplay.qchess.client.view.model.ViewBoard;

public class ClientGUI implements IClientView {

    private final MainFrame mf;

    public ClientGUI() {
        mf = new MainFrame();
    }

    @Override
    public void startView() {
        ClientController.setView(this);
        mf.createStartFrame();
    }

    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public void showMessage(String message) {
        mf.getTable().showMessage(message);
    }

    @Override
    public void drawBoard() {
        mf.getTable().repaint();
    }

    @Override
    public ViewBoard getBoard() {
        throw new UnsupportedOperationException("GUI не поддерживает доску");
    }

    @Override
    public void closeGame(String reason) {
        if (mf.getTable() != null) mf.getTable().closeGame(reason);
    }

    @Override
    public void disconnect(String reason) {
        print(reason);
    }

    @Override
    public void changeMyColorOnBoard(boolean color) {
        mf.getTable().setMyColor(color);
        mf.getTable().remakeBoardPanel();
        mf.getTable().repaint();
    }

    @Override
    public void close() {}
}
