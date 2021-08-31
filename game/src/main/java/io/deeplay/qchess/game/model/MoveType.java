package io.deeplay.qchess.game.model;

import java.io.Serializable;

/**
 * Типы ходов (упорядочены по возрастанию от менее важного к более, т.е. тип менее важного хода <
 * типа более важного хода)
 */
public enum MoveType implements Serializable {
    // Для рокировки
    SHORT_CASTLING(10),
    LONG_CASTLING(10),
    // Для длинного первого хода пешки
    LONG_MOVE(10),
    // Обычный ход
    QUIET_MOVE(10),
    // Для взятия на проходе
    EN_PASSANT(20),
    // Атака фигуры
    ATTACK(20),
    // Для простого хода-превращения пешки
    TURN_INTO(30),
    // Для атакующего хода-превращения пешки
    TURN_INTO_ATTACK(40);

    public final int importantLevel;

    MoveType(final int importantLevel) {
        this.importantLevel = importantLevel;
    }
}
