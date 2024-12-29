package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;

import java.awt.*;
import java.util.List;

public class Iterative extends MiniMax
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
        Point move = moveList.getFirst().getPoint();
        Point trueMove = move;
        
        if(status.isGameOver()){
            return move;
        }
        for (int depth = 1; depth <= maxDepth && !stop; depth++) {

            int bestScore = Integer.MIN_VALUE;

            for (MoveNode mn : moveList) {
                if (stop) {break;}

                HexGameStatus newStatus = new HexGameStatus(status);
                newStatus.placeStone(mn.getPoint());

                /*if (newStatus.isGameOver()) {
                    return mn.getPoint();
                }*/

                int score = getBestScore(newStatus, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
                if (!stop && score > bestScore) {
                    bestScore = score;
                    move = mn.getPoint();
                }
            }

            if (!stop) {
                currentDepth = depth;
                trueMove = move;
            }
        }

        stop = false;
        return trueMove;
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
