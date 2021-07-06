package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.exceptions.ChessException;
import io.deeplay.qchess.game.model.Move;

public interface IPlayer {

    Move getNextMove() throws ChessException;
}