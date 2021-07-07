package io.deeplay.qchess.game.model;

public enum MoveType {
    // Атака фигуры
    ATTACK,
    // Обычный ход
    SIMPLE_STEP,
    // Для длинного первого хода пешки
    LONG_MOVE,
    // Для взятия на проходе
    EN_PASSANT,
    // Для рокировки
    CASTLING,
    // Для последнего хода-превращения пешки
    TURN_INTO
}