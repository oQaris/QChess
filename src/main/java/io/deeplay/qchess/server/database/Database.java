package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.model.Board.BoardFilling;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.server.controller.ServerController;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private static Database database;

    /** sessionToken -> ID */
    private final Map<String, Integer> clients;

    private final Room room;

    private Database() {
        clients = new HashMap<>(ServerController.getMaxClients());
        room = new Room(BoardFilling.STANDARD);
    }

    public static synchronized Database getInstance() {
        if (database == null) database = new Database();
        return database;
    }

    public synchronized Room getRoom() {
        return room;
    }

    public synchronized void addPlayer(String sessionToken, int clientID) {
        clients.put(sessionToken, clientID);
    }

    public synchronized boolean contains(String sessionToken) {
        return clients.get(sessionToken) != null;
    }

    public synchronized void removePlayer(String sessionToken) {
        clients.remove(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public synchronized Integer getID(String sessionToken) {
        return clients.get(sessionToken);
    }

    /** @return цвет игрока или null, если игрок не найден */
    public synchronized Color getColor(String sessionToken) {
        return room.getColor(sessionToken);
    }
}
