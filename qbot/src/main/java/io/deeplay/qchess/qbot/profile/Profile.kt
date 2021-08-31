package io.deeplay.qchess.qbot.profile

import com.google.gson.annotations.SerializedName
import io.deeplay.qchess.game.model.Move
import java.io.Serializable

//@Serializable
class Profile : Serializable {
    @SerializedName("states")
    val states = mutableMapOf<String, MoveWithFreq>()

    fun update(fen: String, move: Move) {
        states.compute(fen) { _, map ->
            map?.moves?.apply {
                compute(move) { _, c -> (c ?: 0) + 1 }
            }
            map ?: MoveWithFreq(mutableMapOf(move to 1))
        }
    }

    fun movesWithFreq(fen: String): MutableMap<Move, Int> {
        return states[fen]?.moves ?: mutableMapOf()
    }

    fun movesWithProb(fen: String): Map<Move, Double> {
        val mwf = states[fen]?.moves ?: return mutableMapOf()
        val sum = mwf.entries.sumOf { ent -> ent.value }
        return mutableMapOf<Move, Double>().apply {
            mwf.forEach {
                put(it.key, it.value.toDouble() / sum)
            }
        }
    }

    companion object {
        @JvmStatic
        fun movesWithProb(mwf: Map<Move, Int>): Map<Move, Double> {
            val sum = mwf.entries.sumOf { ent -> ent.value }
            return mutableMapOf<Move, Double>().apply {
                mwf.forEach {
                    put(it.key, it.value.toDouble() / sum)
                }
            }
        }
    }
}
