package io.deeplay.qchess.core.profile

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Profiles : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 64).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

object States : Table() {
    val id = integer("id").autoIncrement()
    val fen = varchar("name", 71)
    val profileId = reference("profile_id", Profiles.id)
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(fen, profileId)
    }
}

object Moves : Table() {
    val id = integer("id").autoIncrement()
    val from = varchar("from", 2)
    val to = varchar("to", 2)
    val type = varchar("type_move", 16) // надо ли?
    val turnInto = varchar("turn_into", 6).nullable()
    val frequency = integer("frequency").default(1)
    val stateId = reference("state_id", States.id)
    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(from, to, turnInto)
    }
}

fun saveAll() {
    Database.connect("jdbc:h2:./db/profiles", driver = "org.h2.Driver")

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.create(Profiles, States, Moves)

        val lobotProfileId = Profiles.insert {
            it[name] = "Lobot"
        } get Profiles.id

        val lobotStateId = States.insert {
            it[fen] = "asldkfj/sdajf/8/ksdf w KQkq"
            it[profileId] = lobotProfileId
        } get States.id

        Moves.insert {
            it[from] = "с2"
            it[to] = "e5"
            it[type] = "QUIET_MOVE"
            it[stateId] = lobotStateId
        }

        Moves.insert {
            it[from] = "h7"
            it[to] = "h8"
            it[type] = "TURN_INTO"
            it[turnInto] = "QUEEN"
            it[stateId] = lobotStateId
        }

        /*Profiles.selectAll().forEach { println(it) }
        States.selectAll().forEach { println(it) }
        Moves.selectAll().forEach { println(it) }*/
    }
}
