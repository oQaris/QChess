package io.deeplay.qchess.server.dao;

import io.deeplay.qchess.server.database.Database;

public class ConnectionControlDAO {

    /** @return true, если токен есть в списке подключенных клиентов */
    public static boolean contains(String sessionToken) {
        return Database.getInstance().contains(sessionToken);
    }

    /** Добавляет sessionToken к подключенным id клиентов */
    public static void addPlayer(String sessionToken, int clientId) {
        Database.getInstance().addPlayer(sessionToken, clientId);
    }

    /**
     * Удаляет sessionToken из подключенных id клиентов
     *
     * <p>TODO: не удаляет из id в менеджере обработчиков
     */
    public static void removePlayer(String sessionToken) {
        Database.getInstance().removePlayer(sessionToken);
    }

    /** @return id клиента или null, если его нет */
    public static Integer getId(String sessionToken) {
        return Database.getInstance().getId(sessionToken);
    }
}
