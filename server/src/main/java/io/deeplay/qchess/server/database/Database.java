package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.server.controller.ServerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private static final transient Database database = new Database();

    /** sessionToken -> id */
    private final Map<String, Integer> clients;

    private final List<Room> rooms;

    private Database() {
        clients = new ConcurrentHashMap<>(ServerController.getMaxClients());
        rooms = new ArrayList<>(ServerController.getMaxClients());
        for (int i = 0; i < ServerController.getMaxClients(); ++i) rooms.add(new Room());
    }

    public static Database getInstance() {
        return database;
    }

    public void addPlayer(String sessionToken, int clientId) {
        clients.put(sessionToken, clientId);
    }

    public boolean contains(String sessionToken) {
        return sessionToken != null && clients.containsKey(sessionToken);
    }

    public void removePlayer(String sessionToken) {
        if (sessionToken != null) clients.remove(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public Integer getId(String sessionToken) {
        return sessionToken != null ? clients.get(sessionToken) : null;
    }

    /** @return комната с предпочитаемыми настройками или null, если комната не найдена */
    public Room findSuitableRoom(String sessionToken, PlayerType enemyType, int gameCount) {
        for (Room room : rooms) {
            synchronized (room.mutex) {
                if (room.contains(sessionToken)) return room;

                if (enemyType == PlayerType.GUI_PLAYER
                        && room.getMaxGames() == gameCount
                        && !room.isFull()) return room;

                if (room.isEmpty()) return room;
            }
        }
        return null;
    }

    /**
     * @return комната с игроком, у которого токен сессии равен sessionToken или null, если комната
     *     не найдена
     */
    public Room getRoom(String sessionToken) {
        for (Room room : rooms) {
            synchronized (room.mutex) {
                if (room.contains(sessionToken)) return room;
            }
        }
        return null;
    }
}
