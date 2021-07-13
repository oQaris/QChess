package io.deeplay.qchess.game.model;

public enum MoveType {
    // Атака фигуры
    ATTACK,
    // Обычный ход
    QUIET_MOVE,
    // Для длинного первого хода пешки
    LONG_MOVE,
    // Для взятия на проходе
    EN_PASSANT,
    // Для рокировки
    SHORT_CASTLING,
    LONG_CASTLING,
    // Для последнего хода-превращения пешки
    TURN_INTO
}
