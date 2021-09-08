package io.deeplay.qchess.qbot.profile

import com.google.gson.Gson
import io.deeplay.qchess.game.GameSettings
import io.deeplay.qchess.game.model.Board
import io.deeplay.qchess.game.model.Cell
import io.deeplay.qchess.game.model.Move
import io.deeplay.qchess.game.model.MoveType
import io.deeplay.qchess.game.model.figures.FigureType
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption


private val profiles = Storage
val profilesMap = mutableMapOf<String, Profile>()

fun parse(file: File, isUseDB: Boolean = false) {
    val buf = file.inputStream().bufferedReader()
    val fpName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val spName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val profileId1 = profiles.getOrPutProfileId(fpName)
    val profileId2 = profiles.getOrPutProfileId(spName)
    val profile1 = profilesMap.getOrPut(fpName) { Profile() }
    val profile2 = profilesMap.getOrPut(spName) { Profile() }
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
                if (isUseDB) {
                    val stateId =
                        profiles.getOrPutStateId(if (isCurP1) profileId1 else profileId2, prewFen)
                    profiles.push(stateId, move)
                } else {
                    (if (isCurP1) profile1 else profile2).update(prewFen, move)
                    isCurP1 = !isCurP1
                }
            }
            // Если FEN
            else if (chnks[0] == "FEN")
                prewFen = chnks[1].trim()
        }
    }
}

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

val toReplace = Paths.get("./data/profiles.json")
val newContents = toReplace.resolveSibling("profiles_temp.json")
val finalProfiles = Paths.get("C:\\Users\\oQaris\\Desktop\\Git\\QChess\\data\\profiles_final.json")

fun fill() {
    walk("C:\\Users\\admin\\Desktop\\Internship\\arch\\logs") { f ->
        parse(
            f
        )
    }
}

fun main() {
    walk("C:\\Users\\admin\\Desktop\\Internship\\arch\\logs") { f ->
        parse(
            f
        )
    }
    // Для вывода на консоль
    /*profilesMap.forEach { (t, u) ->
        println(
            "$t:\n${
                u.states.map { (k, v) -> "${k.padEnd(64)} -> ${v.map { entry -> entry.key.toStr() + " = " + entry.value }}" }
                    .joinToString("\n")
            }"
        )
    }*/
    println("Идёт сохранение в файл...")
    Files.newBufferedWriter(
        newContents,
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING
    ).use { writer ->
        Gson().toJson(profilesMap, HashMap::class.java, writer)
    }
    println("Перемещение...")
    Files.move(
        newContents,
        toReplace,
        StandardCopyOption.REPLACE_EXISTING,
        StandardCopyOption.ATOMIC_MOVE
    )
    println("Успешно!")

    //profiles.showAll()
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
