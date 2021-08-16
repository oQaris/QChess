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
    @Deprecated public final Object mutex = new Object();

    private RemotePlayer player1;
    private RemotePlayer player2;
    private Selfplay game;
    private int gameCount;
    private int maxGames;
    private GameSettings gs;
    private boolean error;

    public void addGameCount(final int count) {
        gameCount += count;
    }

    public int getGameCount() {
        return gameCount;
    }

    public int getMaxGames() {
        return maxGames;
    }

    public void setGameSettings(final GameSettings gs, final int maxGames) {
        synchronized (mutex) {
            this.gs = gs;
            this.maxGames = maxGames;
        }
    }

    public GameSettings getGameSettings() {
        synchronized (mutex) {
            return gs;
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
                if (player1.getColor() == Color.BLACK) {
                    final RemotePlayer temp = player1;
                    player1 = player2;
                    player2 = temp;
                }
                game = new Selfplay(gs, player1, player2);
            } catch (final ChessError chessError) {
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

    public void addPlayer(final RemotePlayer player) {
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
    public RemotePlayer getPlayer(final String sessionToken) {
        synchronized (mutex) {
            if (player1 != null && player1.getSessionToken().equals(sessionToken)) return player1;
            if (player2 != null && player2.getSessionToken().equals(sessionToken)) return player2;
            return null;
        }
    }

    /** @return true, если в комнате есть игрок с заданным токеном */
    public boolean contains(final String sessionToken) {
        synchronized (mutex) {
            return getPlayer(sessionToken) != null;
        }
    }

    /**
     * Изменяет флаг error = true, если при ходе возникла критическая ошибка
     *
     * @return true, если ход корректный, иначе false
     */
    public boolean move(final Move move) {
        synchronized (mutex) {
            try {
                return game.move(move);
            } catch (final ChessError chessError) {
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
            error = false;
        }
    }

    /** Меняет цвет игрокам и сбрасывает игру */
    public void resetGame() {
        synchronized (mutex) {
            final RemotePlayer temp = player1;
            player1 = player2;
            player2 = temp;

            try {
                gs = gs.newWithTheSameSettings();
                player1.setGameSettings(gs, Color.WHITE);
                player2.setGameSettings(gs, Color.BLACK);
                game = new Selfplay(gs, player1, player2);
            } catch (final ChessError chessError) {
                // Клонирование настроек безопасно, если было до этого создано успешно
            }

            error = false;
        }
    }

    /**
     * @return токен сессии клиента противника для клиента с sessionToken. Вернет null, если его нет
     */
    public String getOpponentSessionToken(final String sessionToken) {
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
    private String getEndGameStatus(final Color color) {
        final boolean isStalemate = gs.endGameDetector.isStalemate(color);
        if (isStalemate && gs.endGameDetector.isCheck(color)) {
            return "Мат " + (color == Color.BLACK ? "черным" : "белым");
        } else if (isStalemate) {
            return "Пат " + (color == Color.BLACK ? "черным" : "белым");
        } else {
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
        }
        return null;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Room room = (Room) o;
        return id == room.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
