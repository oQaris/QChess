package io.deeplay.qchess.core.profile

import io.deeplay.qchess.game.GameSettings
import io.deeplay.qchess.game.model.Board
import io.deeplay.qchess.game.model.Cell
import io.deeplay.qchess.game.model.Move
import io.deeplay.qchess.game.model.MoveType
import io.deeplay.qchess.game.model.figures.FigureType
import java.io.File
import java.nio.file.Paths

private val profiles = Storage

fun parse(file: File) {
    val buf = file.inputStream().bufferedReader()
    val fpName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val spName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val p1 = profiles.getOrPutProfileId(fpName)
    val p2 = profiles.getOrPutProfileId(spName)
    var isCurP1 = true
    var prewFen = GameSettings(Board.BoardFilling.STANDARD).history.getBoardToStringForsythEdwards()

    buf.useLines { lines ->
        lines.forEach { line ->
            val chnks = line.split(":")
            // Если ход
            if (chnks.size == 3) {
                val fromTo = chnks[1].drop(1).take(5).split("-")
                val moveType = chnks[1].drop(8).takeWhile { it != ')' }
                val move =
                    Move(MoveType.valueOf(moveType), Cell.parse(fromTo[0]), Cell.parse(fromTo[1]))
                if (move.moveType == MoveType.TURN_INTO || move.moveType == MoveType.TURN_INTO_ATTACK) {
                    val turnInto = chnks[1].dropLast(8).takeLastWhile { !it.isWhitespace() }
                    move.turnInto = FigureType.valueOf(turnInto)
                }
                val stateId = profiles.getOrPutStateId(if (isCurP1) p1 else p2, prewFen)
                profiles.push(stateId, move)
                isCurP1 = !isCurP1
            }
            // Если FEN
            else if (chnks[0] == "FEN")
                prewFen = chnks[1].trim()
        }
    }
}

private val toReplace = Paths.get("profiles.txt")
private val newContents = toReplace.resolveSibling("temp_profile.json")

fun walk(path: String, action: (File) -> Unit) {
    val root = File(path)
    val list = root.listFiles() ?: return
    for (f in list) {
        if (f.isDirectory) {
            walk(f.absolutePath, action)
        } else {
            println(f.name)
            action(f)
        }
    }
}

fun main() {
    walk("C:\\Users\\admin\\Desktop\\Internship\\arch\\logs\\QNegaMaxBot-3-tt-pesto_VS_NNNBot-228") { f -> parse(f) }
    /*profiles.forEach { (t, u) ->
        println(
            "$t:\n${
                u.states.map { (k, v) -> "${k.padEnd(64)} -> ${v.map { entry -> entry.key.toStr() + " = " + entry.value }}" }
                    .joinToString("\n")
            }"
        )
    }
    println("Идёт сохранение в файл...")
    Files.newBufferedWriter(
        newContents,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    ).use { writer -> Gson().toJson(profiles, writer) }
    println("Перемещение...")
    Files.move(
        newContents,
        toReplace,
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.ATOMIC_MOVE
    )
    println("Успешно!")*/

    profiles.showAll()
}

fun Move.toStr(): String {
    val sb =
        StringBuilder()
            .append(from)
            .append("-")
            .append(to)
    return when (moveType) {
        MoveType.TURN_INTO, MoveType.TURN_INTO_ATTACK -> sb.append("->")
            .append(turnInto)
        else -> sb
    }.toString()
}
