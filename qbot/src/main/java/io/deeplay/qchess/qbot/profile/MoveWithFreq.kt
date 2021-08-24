package io.deeplay.qchess.qbot.profile

import io.deeplay.qchess.game.model.Move
import java.io.Serializable

data class MoveWithFreq( val moves: MutableMap<Move, Int> = mutableMapOf()):Serializable