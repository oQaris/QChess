package io.deeplay.qchess.game.model;

public enum MoveType {
    // Атака фигуры
    ATTACK,
    // Обычный ход
    SIMPLE_STEP,
    // Для взятия на проходе, длинного первого хода пешки и рокировки
    SPECIAL_MOVE,
    // Для последнего хода-превращения пешки
    TURN_INTO
}