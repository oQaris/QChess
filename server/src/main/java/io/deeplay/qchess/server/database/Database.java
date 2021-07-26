package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.player.PlayerType;
import io.deeplay.qchess.server.controller.ServerController;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private static final transient Database database = new Database();

    /** sessionToken -> Id */
    private final Map<String, Integer> clients;

    /** Не поддерживает потокобезопасное заполнение/удаление комнат TODO this (?) */
    private final TreeSet<Room> rooms;

    @Deprecated(forRemoval = true)
    private final Room room = new Room();

    private Database() {
        clients = new ConcurrentHashMap<>(ServerController.getMaxClients());
        rooms = new TreeSet<>(Comparator.comparingInt(r -> r.id));

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
        if (room.contains(sessionToken)) return room;
        if (enemyType == PlayerType.REMOTE_PLAYER) return room.isFull() ? null : room;
        return room.isEmpty() ? room : null;
    }

    /**
     * @return комната с игроком, у которого токен сессии равен sessionToken или null, если комната
     *     не найдена
     */
    public Room getRoom(String sessionToken) {
        return room.contains(sessionToken) ? room : null;
    }
}
