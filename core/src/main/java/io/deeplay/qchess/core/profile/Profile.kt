package io.deeplay.qchess.core.profile

import io.deeplay.qchess.game.model.Move

class Profile {
    val states = HashMap<String, MutableMap<Move, Int>>()

    fun update(fen: String, move: Move) {
        states.compute(fen) { _, map ->
            map?.apply {
                compute(move) { _, c -> (c ?: 0) + 1 }
            } ?: mutableMapOf(move to 1)
        }
    }

    fun movesWithFreq(fen: String): Set<MutableMap.MutableEntry<Move, Int>> {
        return states[fen]?.entries ?: emptySet()
    }

    fun movesWithProb(fen: String): List<Pair<Move, Double>> {
        val mwf = movesWithFreq(fen)
        val sum = mwf.sumOf { ent -> ent.value }
        return mwf.map { ent -> ent.key to ent.value.toDouble() / sum }
    }
}