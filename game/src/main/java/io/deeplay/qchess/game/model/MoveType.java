package io.deeplay.qchess.game.model;

import java.io.Serializable;

/**
 * Типы ходов (упорядочены по возрастанию от менее важного к более, т.е. тип менее важного хода <
 * типа более важного хода)
 */
public enum MoveType implements Serializable {
    // Для рокировки
    SHORT_CASTLING(20),
    LONG_CASTLING(20),
    // Для длинного первого хода пешки
    LONG_MOVE(15),
    // Обычный ход
    QUIET_MOVE(10),
    // Для взятия на проходе
    EN_PASSANT(30),
    // Атака фигуры
    ATTACK(30),
    // Для простого хода-превращения пешки
    TURN_INTO(30),
    // Для атакующего хода-превращения пешки
    TURN_INTO_ATTACK(30);

    public final int importantLevel;

    MoveType(final int importantLevel) {
        this.importantLevel = importantLevel;
    }
}
