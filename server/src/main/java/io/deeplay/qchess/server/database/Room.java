package io.deeplay.qchess.server.database;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.Selfplay;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.logics.EndGameDetector;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.game.player.RemotePlayer;

/**
 * Является потокобезопасным, также можно получить мьютекс для блокировки класса. Содержит
 * уникальный идентификатор комнаты, поэтому функции equals и hashCode могут выполняться параллельно
 * без блокировки
 */
public class Room {
    private static int lastId;
    public final int id = lastId++;

    /**
     * @deprecated TODO: заменить на фабрику мьютексов в БД после изменения способа создания комнат.
     *     (Создавать новые комнаты, а не использовать существующие). Пример, где это надо {@link
     *     io.deeplay.qchess.server.service.GameService#endGameForOpponentOf тут} и {@link
     *     io.deeplay.qchess.server.service.MatchMaking#findGame там}, и {@link
     *     io.deeplay.qchess.server.service.GameService#action здесь}
     */
    @Deprecated(forRemoval = true)
    public final Object mutex = new Object();

    private RemotePlayer player1;
    private RemotePlayer player2;
    private Selfplay game;
    private GameSettings gs;
    private boolean error;

    public void setGameSettings(GameSettings gs) {
        synchronized (mutex) {
            this.gs = gs;
        }
    }

    public boolean isStarted() {
        synchronized (mutex) {
            return game != null;
        }
    }

    public boolean isFinished() {
        synchronized (mutex) {
            return game == null
                    || getEndGameStatus(game.getCurrentPlayerToMove().getColor().inverse()) != null;
        }
    }

    /** Изменяет флаг error = true, если при создании игры возникла критическая ошибка */
    public void startGame() {
        synchronized (mutex) {
            try {
                game = new Selfplay(gs, player1, player2);
            } catch (ChessError chessError) {
                error = true;
            }
        }
    }

    /** @return true, если возникли критические ошибки при игре */
    public boolean isError() {
        synchronized (mutex) {
            return error;
        }
    }

    public void addPlayer(RemotePlayer player) {
        synchronized (mutex) {
            if (player1 == null) player1 = player;
            else if (player2 == null) player2 = player;
        }
    }

    /** @return true, если комната заполнена */
    public boolean isFull() {
        synchronized (mutex) {
            return player1 != null && player2 != null;
        }
    }

    /** @return true, если комната пустая */
    public boolean isEmpty() {
        synchronized (mutex) {
            return player1 == null && player2 == null;
        }
    }

    /** @return токен сессии первого (белого) игрока */
    public String getFirstPlayerToken() {
        synchronized (mutex) {
            return player1.getSessionToken();
        }
    }

    /** @return токен сессии второго (черного) игрока */
    public String getSecondPlayerToken() {
        synchronized (mutex) {
            return player2.getSessionToken();
        }
    }

    /** @return первый (белый) игрок */
    public RemotePlayer getFirstPlayer() {
        synchronized (mutex) {
            return player1;
        }
    }

    /** @return второй (черный) игрок */
    public RemotePlayer getSecondPlayer() {
        synchronized (mutex) {
            return player2;
        }
    }

    /** @return игрок с заданным токеном или null, если его нет в этой комнате */
    public RemotePlayer getPlayer(String sessionToken) {
        synchronized (mutex) {
            if (player1 != null && player1.getSessionToken().equals(sessionToken)) return player1;
            if (player2 != null && player2.getSessionToken().equals(sessionToken)) return player2;
            return null;
        }
    }

    /** @return true, если в комнате есть игрок с заданным токеном */
    public boolean contains(String sessionToken) {
        synchronized (mutex) {
            return getPlayer(sessionToken) != null;
        }
    }

    /**
     * Изменяет флаг error = true, если при ходе возникла критическая ошибка
     *
     * @return true, если ход корректный, иначе false
     */
    public boolean move(Move move) {
        synchronized (mutex) {
            try {
                return game.move(move);
            } catch (ChessError chessError) {
                error = true;
                return false;
            }
        }
    }

    /** Удаляет всех из комнаты и сбрасывает настройки игры */
    public void resetRoom() {
        synchronized (mutex) {
            player1 = null;
            player2 = null;
            gs = null;
            game = null;
        }
    }

    /**
     * @return токен сессии клиента противника для клиента с sessionToken. Вернет null, если его нет
     */
    public String getOpponentSessionToken(String sessionToken) {
        synchronized (mutex) {
            if (player1 != null && player1.getSessionToken().equals(sessionToken)) {
                if (player2 != null) return player2.getSessionToken();
                else return null;
            } else if (player2 != null && player2.getSessionToken().equals(sessionToken)) {
                if (player1 != null) return player1.getSessionToken();
                else return null;
            }
            return null;
        }
    }

    /** @return строка с причиной окончания игры или null, если игра еще не окончена */
    public String getEndGameStatus() {
        return getEndGameStatus(game.getCurrentPlayerToMove().getColor());
    }

    /** @return статус конца игры для игрока цвета color или null, если игра еще не окончена */
    private String getEndGameStatus(Color color) {
        if (gs.endGameDetector.isDraw()) {
            if (gs.endGameDetector.isDrawWithPeaceMoves()) {
                return String.format(
                        "Ничья: %d ходов без взятия и хода пешки",
                        EndGameDetector.END_PEACE_MOVE_COUNT);
            } else if (gs.endGameDetector.isDrawWithRepetitions()) {
                return String.format(
                        "Ничья: %d повторений позиций доски",
                        EndGameDetector.END_REPETITIONS_COUNT);
            } else if (gs.endGameDetector.isDrawWithNotEnoughMaterialForCheckmate()) {
                return "Ничья: недостаточно фигур, чтобы поставить мат";
            }
        } else {
            if (gs.endGameDetector.isCheckmate(color)) {
                return "Мат " + (color == Color.BLACK ? "черным" : "белым");
            } else if (gs.endGameDetector.isStalemate(color)) {
                return "Пат " + (color == Color.BLACK ? "черным" : "белым");
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
