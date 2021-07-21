package io.deeplay.qchess.client.view.gui;

public class MainFrame {
    private ConnectFrame connectFrame;
    private ChoosePlayerFrame choosePlayerFrame;
    private ChooseStyleFrame chooseStyleFrame;
    private Table table;
    private int enemyNumber;
    private String style;
    private boolean color;

    public void createConnectFrame() {
        connectFrame = new ConnectFrame(this);
    }

    public void createChoosePlayerFrame(boolean color) {
        this.color = color;
        choosePlayerFrame = new ChoosePlayerFrame(this);
    }

    public void createChooseStyleFrame(int enemyNumber) {
        this.enemyNumber = enemyNumber;
        chooseStyleFrame = new ChooseStyleFrame(this);
    }

    public void createTable(String style) {
        this.style = style;
        table = new Table(style, color, this);
    }

    public void destroyConnectFrame() {
        connectFrame.destroy();
        connectFrame = null;
    }

    public void destroyChoosePlayerFrame() {
        choosePlayerFrame.destroy();
        choosePlayerFrame = null;
    }

    public void destroyChooseStyleFrame() {
        chooseStyleFrame.destroy();
        chooseStyleFrame = null;
    }

    public void destroyTable() {
        table.destroy();
        table = null;
    }

    public Table getTable() {
        return table;
    }

    public int getEnemyNumber() {
        return enemyNumber;
    }

    public String getStyle() {
        return style;
    }

    public boolean isColor() {
        return color;
    }
}
