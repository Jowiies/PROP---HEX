package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;

import java.awt.*;
import java.util.List;

public class Basic extends MiniMax
{
    public Basic(int maxDepth)
    {
        super(maxDepth);
    }

    @Override
    public Point findBestMove(HexGameStatus status)
    {
        exploratedNodes = 0;
        PlayerType player = status.getCurrentPlayer();
        List<MoveNode> moveList = status.getMoves();
        int bestScore = Integer.MIN_VALUE;
        Point move = moveList.getFirst().getPoint();

        for (MoveNode mn : moveList) {
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());

            if (newStatus.isGameOver()) {
                return mn.getPoint();
            }

            int score = getBestScore(newStatus, maxDepth-1, bestScore, Integer.MAX_VALUE, false, player);

            if (score > bestScore) {
                bestScore = score;
                move = mn.getPoint();
            }
        }

        return move;
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
