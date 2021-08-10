package io.deeplay.qchess.game.model;

/**
 * Типы ходов (упорядочены по возрастанию от менее важного к более, т.е. тип менее важного хода <
 * типа более важного хода)
 */
public enum MoveType {
    // Для рокировки
    SHORT_CASTLING(0),
    LONG_CASTLING(0),
    // Для длинного первого хода пешки
    LONG_MOVE(10),
    // Обычный ход
    QUIET_MOVE(20),
    // Для взятия на проходе
    EN_PASSANT(30),
    // Атака фигуры
    ATTACK(40),
    // Для простого хода-превращения пешки
    TURN_INTO(50),
    // Для атакующего хода-превращения пешки
    TURN_INTO_ATTACK(60);

    public final int importantLevel;

    MoveType(final int importantLevel) {
        this.importantLevel = importantLevel;
    }
}
