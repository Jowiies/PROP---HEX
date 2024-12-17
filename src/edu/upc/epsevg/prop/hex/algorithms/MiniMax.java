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
        if (stop) {return 0;}

        List<MoveNode> moveList = status.getMoves();
        if (depth == 0) {
            exploratedNodes++;
            return heuristic.getValue(status);
        }

        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (MoveNode mn : moveList) {
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());
            if (newStatus.isGameOver()) {
                return isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax);

            if (stop) {return 0;}

            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            if(isMax) alpha = Math.max(alpha, bestScore);
            else beta = Math.min(beta, bestScore);

            if (alpha >= beta) {break;}
        }

        return bestScore;
    }
}
