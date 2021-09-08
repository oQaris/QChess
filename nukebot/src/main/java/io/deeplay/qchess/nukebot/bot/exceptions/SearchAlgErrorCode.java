package io.deeplay.qchess.nukebot.bot.exceptions;

public enum SearchAlgErrorCode {
    SEARCH_ALG("Возникло исключение в алгоритме поиска");

    public final String message;

    SearchAlgErrorCode(final String message) {
        this.message = message;
    }
}
