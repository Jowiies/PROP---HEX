package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;

import java.awt.*;
import java.util.List;

public class Basic extends MinMax
{
    public Basic(int maxDepth)
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

        for (MoveNode mn : moveList) {
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());

            if (newStatus.isGameOver()) {
                return mn.getPoint();
            }

            int score = getBestScore(newStatus, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (score > bestScore) {
                bestScore = score;
                move = mn.getPoint();
            }
        }

        return move;
    }

    private int getBestScore(HexGameStatus status, int depth, int alpha, int beta, boolean isMax)
    {
        exploratedNodes++;

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
        //NOTHING
    }

    @Override
    public long getExplorationDepth()
    {
        return exploratedNodes;
    }

    @Override
    public int getMaxDepth()
    {
        return maxDepth;
    }
}
