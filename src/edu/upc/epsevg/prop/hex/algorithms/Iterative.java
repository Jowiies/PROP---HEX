package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;

import java.awt.*;
import java.util.List;

public class Iterative extends MinMax
{
    private int currentDepth;

    public Iterative(int maxDepth)
    {
        super(maxDepth);
    }

    @Override
    public Point findBestMove(HexGameStatus status)
    {
        exploratedNodes = 0;
        List<MoveNode> moveList = status.getMoves();
        int bestScore = Integer.MIN_VALUE;
        Point move = moveList.getFirst().getPoint();
        for (int depth = 1; depth <= maxDepth; depth++) {
            if (stop) {break;}

            currentDepth = depth;
            for (MoveNode mn : moveList) {
                if (stop) {break;}

                HexGameStatus newStatus = new HexGameStatus(status);
                newStatus.placeStone(mn.getPoint());

                if (newStatus.isGameOver()) {
                    return mn.getPoint();
                }

                int score = getBestScore(newStatus, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

                if (score > bestScore) {
                    bestScore = score;
                    move = mn.getPoint();
                }
            }
        }

        stop = false;
        return move;
    }

    private int getBestScore(HexGameStatus status, int depth, int alpha, int beta, boolean isMax)
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

            if (stop) {return 0;}

            int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax);
            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            alpha = isMax ? Math.max(alpha, bestScore) : alpha;
            beta = !isMax ? Math.min(beta, bestScore) : beta;

            if (beta <= alpha) {break;}
        }

        return bestScore;
    }

    @Override
    public void stop()
    {
        this.stop = true;
    }

    @Override
    public long getExplorationDepth()
    {
        return exploratedNodes;
    }

    @Override
    public int getMaxDepth()
    {
        return currentDepth;
    }
}
