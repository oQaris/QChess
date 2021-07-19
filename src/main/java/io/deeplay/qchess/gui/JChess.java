package io.deeplay.qchess.gui;

import io.deeplay.qchess.client.IClientController;
import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Board;

public class JChess {
    public static void main(String[] args) throws ChessException, ChessError {
        GameSettings gs = new GameSettings("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        IClientController cc = new GuiController(gs);

        //ConnectFrame connectFrame = new ConnectFrame();
        Table tableWhite = new Table("twostyle", true, cc);
        Table tableBlack = new Table("onestyle", false, cc);
    }
}
