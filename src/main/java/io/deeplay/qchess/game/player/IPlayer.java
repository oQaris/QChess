package io.deeplay.qchess.game.player;

import io.deeplay.qchess.game.model.Board;
import io.deeplay.qchess.game.model.Move;

public interface IPlayer {

    Move getMove(Board board);
}