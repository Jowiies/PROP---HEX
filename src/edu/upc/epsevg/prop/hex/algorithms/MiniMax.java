package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;

import java.awt.*;
import java.util.List;

public abstract class MiniMax
{
    protected boolean stop;
    protected int maxDepth;
    protected long exploratedNodes;
    private final Heuristic heuristic = new Heuristic();

    public MiniMax(int maxDepth)
    {
        stop = false;
        this.maxDepth = maxDepth;
    }

    public abstract Point findBestMove(HexGameStatus status);
    public abstract void stop();
    public abstract long getExplorationDepth();
    public abstract int getMaxDepth();

    protected int getBestScore(HexGameStatus status, int depth, int alpha, int beta, boolean isMax)
    {
        exploratedNodes++;

        if (stop) {return isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;}

        List<MoveNode> moveList = status.getMoves();
        if (depth == 0 || moveList.isEmpty()) {
            return 0;
        }

        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (MoveNode mn : moveList) {
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());

            if (newStatus.isGameOver()) {
                return isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            if (stop) {return isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;}

            int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax);
            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            alpha = isMax ? Math.max(alpha, bestScore) : alpha;
            beta = !isMax ? Math.min(beta, bestScore) : beta;

            if (beta <= alpha) {break;}
        }

        return bestScore;
    }
}
