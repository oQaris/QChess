package io.deeplay.qchess.nukebot.bot.searchalg;

import io.deeplay.qchess.game.GameSettings;
import io.deeplay.qchess.game.exceptions.ChessError;
import io.deeplay.qchess.game.model.Color;
import io.deeplay.qchess.game.model.Move;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.EvaluationFunc;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluation;
import io.deeplay.qchess.nukebot.bot.evaluationfunc.commoneval.CommonEvaluationConstructor;
import io.deeplay.qchess.nukebot.bot.features.MoveSorter;
import io.deeplay.qchess.nukebot.bot.features.components.WrapperGuard;
import io.deeplay.qchess.nukebot.bot.searchfunc.ResultUpdater;
import io.deeplay.qchess.nukebot.bot.searchfunc.SearchFunc;
import java.util.List;

/**
 * Общий класс для алгоритмов поиска с альфа-бета отсечениями. Совместим с параллельным поиском
 * благодаря {@link ResultUpdater}
 */
public abstract class SearchAlgorithm<T extends SearchAlgorithm<? super T>> extends WrapperGuard<T>
        implements AlgBase, ISearchAlgorithm {

    protected final Color myColor;
    protected final Color enemyColor;
    protected final CommonEvaluation commonEvaluation;
    protected final ResultUpdater resultUpdater;
    protected final MoveSorter moveSorter;
    protected int maxDepth;
    protected int moveVersion;
    protected Move mainMove;
    protected GameSettings gs;

    protected SearchAlgorithm(
            final SearchFunc<?> searchFunc,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final int maxDepth) {
        super(null);
        resultUpdater = searchFunc.resultUpdater;
        this.mainMove = mainMove;
        this.moveVersion = moveVersion;
        this.gs = gs;
        commonEvaluation = searchFunc.commonEvaluation;
        myColor = searchFunc.myColor;
        enemyColor = searchFunc.enemyColor;
        this.maxDepth = maxDepth;
        moveSorter = searchFunc.moveSorter;
    }

    protected SearchAlgorithm(
            final ResultUpdater resultUpdater,
            final Move mainMove,
            final int moveVersion,
            final GameSettings gs,
            final Color myColor,
            final CommonEvaluationConstructor commonEvaluationConstructor,
            final EvaluationFunc evaluationFunc,
            final int maxDepth) {
        super(null);
        this.resultUpdater = resultUpdater;
        this.mainMove = mainMove;
        this.moveVersion = moveVersion;
        this.gs = gs;
        commonEvaluation = commonEvaluationConstructor.newInstance(this, evaluationFunc);
        this.myColor = myColor;
        enemyColor = myColor.inverse();
        this.maxDepth = maxDepth;
        moveSorter = new MoveSorter(gs.board);
    }

    protected SearchAlgorithm(final T alg) {
        super(alg);
        resultUpdater = alg.resultUpdater;
        mainMove = alg.mainMove;
        moveVersion = alg.moveVersion;
        gs = alg.gs;
        commonEvaluation = alg.commonEvaluation;
        myColor = alg.myColor;
        enemyColor = alg.enemyColor;
        maxDepth = alg.maxDepth;
        moveSorter = new MoveSorter(gs.board);
    }

    public void setSettings(
            final Move mainMove, final GameSettings gs, final int maxDepth, final int moveVersion) {
        this.mainMove = mainMove;
        this.gs = gs;
        this.maxDepth = maxDepth;
        this.moveVersion = moveVersion;
    }

    public GameSettings getGameSettings() {
        return gs;
    }

    public Color getMyColor() {
        return myColor;
    }

    public Color getEnemyColor() {
        return enemyColor;
    }

    @Override
    public void makeMove(final Move move) throws ChessError {
        makeMove(move, true, true);
    }

    @Override
    public void makeMove(
            final Move move, final boolean useHistoryRecord, final boolean changeMoveSideInRecord)
            throws ChessError {
        gs.moveSystem.move(move, useHistoryRecord, changeMoveSideInRecord);
    }

    @Override
    public void undoMove() throws ChessError {
        undoMove(true);
    }

    @Override
    public void undoMove(final boolean useHistoryRecord) throws ChessError {
        gs.moveSystem.undoMove(useHistoryRecord);
    }

    @Override
    public List<Move> getLegalMoves(final Color color) throws ChessError {
        return gs.board.getAllPreparedMoves(gs, color);
    }

    @Override
    public void prioritySort(final List<Move> allMoves) {
        allMoves.sort(MoveSorter.movesPriority);
        // moveSorter.allSorts(allMoves);
    }

    @Override
    public boolean isTerminalNode(final List<Move> allMoves) {
        return allMoves.isEmpty() || gs.endGameDetector.isDraw();
    }

    @Override
    public boolean isCheck(final Color color) {
        return gs.endGameDetector.isCheck(color);
    }

    @Override
    public boolean isStalemate(final Color color) {
        return gs.endGameDetector.isStalemate(color);
    }

    @Override
    public boolean isDraw() {
        return gs.endGameDetector.isDraw();
    }

    /**
     * @param allMoves все ходы текущего игрока
     * @param isMyMove true, если сейчас ход максимизирующего игрока
     * @param depth текущая глубина
     * @return оценка доски для максимизирующего игрока
     */
    public int getEvaluation(
            final List<Move> allMoves,
            final boolean isMyMove,
            final int alfa,
            final int beta,
            final int depth)
            throws ChessError {
        return getEvaluation(
                isCheck(myColor), isCheck(enemyColor), allMoves, isMyMove, alfa, beta, depth);
    }

    /**
     * @param isCheckToMe поставлен ли шах максимизирующему игроку
     * @param isCheckToEnemy поставлен ли шах минимизирующему игроку
     * @param allMoves возможно все ходы текущего игрока
     * @param isMyMove true, если сейчас ход максимизирующего игрока
     * @param depth текущая глубина
     * @return оценка доски для максимизирующего игрока
     */
    public final int getEvaluation(
            final boolean isCheckToMe,
            final boolean isCheckToEnemy,
            final List<Move> allMoves,
            final boolean isMyMove,
            final int alfa,
            final int beta,
            final int depth)
            throws ChessError {
        return commonEvaluation.getEvaluation(
                isCheckToMe, isCheckToEnemy, allMoves, isMyMove, alfa, beta, depth);
    }

    @Override
    public void updateResult(final int estimation) {
        resultUpdater.updateResult(mainMove, estimation, maxDepth, moveVersion);
    }

    @Override
    public boolean isInvalidMoveVersion() {
        return resultUpdater.isInvalidMoveVersion(moveVersion);
    }
}
