package io.deeplay.qchess.qbot.profile

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.MapSerializer
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import io.deeplay.qchess.game.model.Move

@Serializable
class Profile {
    val states = HashMap<String, MutableMap<Move, Int>>()

    fun update(fen: String, move: Move) {
        states.compute(fen) { _, map ->
            map?.apply {
                compute(move) { _, c -> (c ?: 0) + 1 }
            } ?: mutableMapOf(move to 1)
        }
    }

    fun movesWithFreq(fen: String): MutableMap<Move, Int> {
        return states[fen] ?: mutableMapOf()
    }

    fun movesWithProb(fen: String): Map<Move, Double> {
        val mwf = states[fen] ?: return mutableMapOf()
        val sum = mwf.entries.sumOf { ent -> ent.value }
        return mutableMapOf<Move, Double>().apply {
            mwf.forEach {
                put(it.key, it.value.toDouble() / sum)
            }
        }
    }

    companion object{
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
