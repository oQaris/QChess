package io.deeplay.qchess.core.profile

import io.deeplay.qchess.game.model.Board.STD_BOARD_SIZE
import io.deeplay.qchess.game.model.Cell
import io.deeplay.qchess.game.model.Move
import io.deeplay.qchess.game.model.MoveType
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Storage {
    init {
        Database.connect("jdbc:h2:./db/profiles", driver = "org.h2.Driver")
        transaction {
            SchemaUtils.create(Profiles, States, Moves)
        }
    }

    object Profiles : Table() {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 64).uniqueIndex()
        override val primaryKey = PrimaryKey(id)
    }

    object States : Table() {
        val id = integer("id").autoIncrement()
        val fen = varchar("name", 70)
        val profileId = reference("profile_id", Profiles.id)
        override val primaryKey = PrimaryKey(id)

        init {
            uniqueIndex(fen, profileId)
        }
    }

    object Moves : Table() {
        val id = integer("id").autoIncrement()
        val from = byte("from_hash")
        val to = byte("to_hash")
        val type = byte("type_move_order") // надо ли?
        val turnInto = byte("turn_into_order").nullable()
        val frequency = short("frequency").default(1)
        val stateId = reference("state_id", States.id)
        override val primaryKey = PrimaryKey(id)

        init {
            uniqueIndex(from, to, turnInto, stateId)
        }
    }

    fun getOrPutProfileId(nameStr: String): Int {
        return transaction {
            Profiles.select { Profiles.name eq nameStr }
                .singleOrNull()?.get(Profiles.id)
                ?: Profiles.insert {
                    it[name] = nameStr
                }[Profiles.id]
        }
    }

    fun getOrPutStateId(profileIdStr: Int, fenStr: String): Int {
        return transaction {
            States.select { (States.profileId eq profileIdStr) and (States.fen eq fenStr) }
                .singleOrNull()?.get(States.id)
                ?: States.insert {
                    it[profileId] = profileIdStr
                    it[fen] = fenStr
                }[States.id]
        }
    }
// val stateId = getOrPutStateId(profileId, fen)

    fun push(stateId: Int, newMove: Move) {
        //addLogger(StdOutSqlLogger)
        val from = newMove.from.toByte()
        val to = newMove.to.toByte()
        val turnInto = newMove.turnInto?.ordinal?.toByte()

        push(stateId, from, to, newMove.moveType.ordinal.toByte(), turnInto)
    }

    fun push(stateId: Int, from: Byte, to: Byte, type: Byte, turnInto: Byte?) {
        transaction {
            val moveQuery =
                Moves.select { (Moves.stateId eq stateId) and (Moves.from eq from) and (Moves.to eq to) and (Moves.turnInto eq turnInto) }
                    .singleOrNull()

            if (moveQuery == null)
                Moves.insert {
                    it[Moves.stateId] = stateId
                    it[Moves.from] = from
                    it[Moves.to] = to
                    it[Moves.type] = type
                    it[Moves.turnInto] = turnInto
                }
            else
                Moves.update({ Moves.id eq moveQuery[Moves.id] }) {
                    with(SqlExpressionBuilder) {
                        it[frequency] = frequency + 1
                    }
                }
        }
    }

    fun get(stateId: Int): List<Pair<Move, Short>> {
        return transaction {
            mutableListOf<Pair<Move, Short>>().apply {
                Moves.select { Moves.stateId eq stateId }.forEach {
                    val from = it[Moves.from]
                    val to = it[Moves.to]
                    val move = Move(
                        MoveType.values()[it[Moves.type].toInt()],
                        Cell(from / STD_BOARD_SIZE, from % STD_BOARD_SIZE),
                        Cell(to / STD_BOARD_SIZE, to % STD_BOARD_SIZE)
                    )
                    add(move to it[Moves.frequency])
                }
            }
        }
    }

    fun showAll() {
        transaction {
            Profiles.show()
            States.show()
            Moves.show()
        }
    }

    private fun Table.show() {
        selectAll().forEach { k -> println("$k") }
        println()
    }
}

