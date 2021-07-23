package io.deeplay.qchess.client.view.gui;

public class EndGame {
    private final String status;
    private final boolean end;

    public EndGame(String status, boolean end) {
        this.status = status;
        this.end = end;
    }

    public String getStatus() {
        return status;
    }

    public boolean isEnd() {
        return end;
    }
}
