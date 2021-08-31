package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.model.Color;
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

    /** Не поддерживает потокобезопасное заполнение/удаление комнат TODO this (?) */
    private final List<Room> rooms;

    private Database() {
        clients = new ConcurrentHashMap<>(ServerController.getMaxClients());
        rooms = new ArrayList<>(ServerController.getMaxClients());
        for (int i = 0; i < ServerController.getMaxClients(); ++i) rooms.add(new Room());
    }

    public static Database getInstance() {
        return database;
    }

    /** Добавляет sessionToken к подключенным id клиентов */
    public void addPlayer(final String sessionToken, final int clientId) {
        clients.put(sessionToken, clientId);
    }

    /** @return true, если токен есть в списке подключенных клиентов */
    public boolean contains(final String sessionToken) {
        return sessionToken != null && clients.containsKey(sessionToken);
    }

    /**
     * Удаляет sessionToken из подключенных id клиентов
     *
     * <p>TODO: не удаляет из id в менеджере обработчиков
     */
    public void removePlayer(final String sessionToken) {
        if (sessionToken != null) clients.remove(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public Integer getId(final String sessionToken) {
        return sessionToken != null ? clients.get(sessionToken) : null;
    }

    /** @return комната с предпочитаемыми настройками или null, если комната не найдена */
    public Room findSuitableRoom(
            final String sessionToken,
            final PlayerType enemyType,
            final int gameCount,
            final Color myPreferColor) {
        final Color enemyColor = myPreferColor != null ? myPreferColor.inverse() : null;
        for (final Room room : rooms) {
            synchronized (room.mutex) {
                if (room.contains(sessionToken)) return room;
                if (room.isEmpty()) return room;
                if (enemyType == PlayerType.REMOTE_PLAYER
                        && room.getMaxGames() == gameCount
                        && !room.isFull()
                        && (myPreferColor == null
                                || room.getFirstPlayer().getColor() == enemyColor)) return room;
            }
        }
        return null;
    }

    /**
     * @return комната с игроком, у которого токен сессии равен sessionToken или null, если комната
     *     не найдена
     */
    public Room getRoom(final String sessionToken) {
        for (final Room room : rooms) {
            synchronized (room.mutex) {
                if (room.contains(sessionToken)) return room;
            }
        }
        return null;
    }
}
