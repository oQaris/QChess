package io.deeplay.qchess.qbot.profile

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import io.deeplay.qchess.game.GameSettings
import io.deeplay.qchess.game.model.Board
import io.deeplay.qchess.game.model.Cell
import io.deeplay.qchess.game.model.Move
import io.deeplay.qchess.game.model.MoveType
import io.deeplay.qchess.game.model.figures.FigureType
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import kotlinx.serialization.json.*


private val profiles = Storage
public val profilesMap = mutableMapOf<String, Profile>()

fun parse(file: File) {
    val buf = file.inputStream().bufferedReader()
    val fpName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val spName = buf.readLine().takeLastWhile { !it.isWhitespace() }
    val p1 = /*profiles.getOrPutProfileId(fpName)*/profilesMap.getOrPut(fpName){ Profile() }
    val p2 = /*profiles.getOrPutProfileId(spName)*/profilesMap.getOrPut(spName){ Profile() }
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
                //val stateId = profiles.getOrPutStateId(if (isCurP1) p1 else p2, prewFen)
                //profiles.push(stateId, move)
                (if (isCurP1) p1 else p2).update(prewFen, move)
                isCurP1 = !isCurP1
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

fun fill(){
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

    /*profiles.getProfiles().forEach { (prfName, prfId) ->
        println("$prfName:")
        profiles.getStates(prfId).forEach { (sttFen, sttId) ->
            println(
                "$sttFen -> ${
                    profiles.getMoves(sttId).joinToString(", ") { (move, freq) -> "$move = $freq" }
                }"
            )
        }
    }*/

    //profiles.showAll()
}

fun pullProfiles(): HashMap<String, Profile> {
        Files.newInputStream(finalProfiles).use { reader ->
            /*val mapper = ObjectMapper()
            val typeRef: TypeReference<HashMap<String, Profile>> =
                object : TypeReference<HashMap<String, Profile>>() {}
            return mapper.readValue(reader, typeRef)*/

            try {
                ObjectInputStream(reader).use { ois ->
                    return ois.readObject() as HashMap<String, Profile>
                }
            } catch (ex: java.lang.Exception) {
                println(ex.message)
            }

            //return Json.decodeFromString<HashMap<String, Profile>>(reader.readText())

            /*return Gson().fromJson<Map<String, Profile>>(
                    reader,
                    object :
                        TypeToken<Map<String, Profile>>() {}.type
                )*/
        }
    return HashMap()
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
