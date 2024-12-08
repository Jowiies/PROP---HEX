package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;

import java.awt.*;
import java.util.List;

public class BogoHex implements IPlayer, IAuto
{
    private final String name = "BogoHexPlayer";
    private boolean stop;
    private boolean ids;
    private int maxDepth;
    private long exploredNodes;
    public BogoHex(int maxDepth, boolean ids)
    {
       this.stop = false;
       this.maxDepth = maxDepth;
       this.ids = ids;
       this.exploredNodes = 0;
    }

    @Override
    public PlayerMove move(HexGameStatus hexGameStatus)
    {
        exploredNodes = 0;
        return new PlayerMove(findBestMove(hexGameStatus), exploredNodes, maxDepth, ids ? SearchType.MINIMAX_IDS : SearchType.MINIMAX);
    }

    @Override
    public void timeout()
    {
        this.stop = true;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    public int MinMax(HexGameStatus status, int depth, int alpha, int beta, boolean isMax)
    {
        List<MoveNode> moveList = status.getMoves();
        exploredNodes++;
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

            int score = MinMax(newStatus, depth - 1, alpha, beta, !isMax);
            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            alpha = isMax ? Math.max(alpha, bestScore) : alpha;
            beta = !isMax ? Math.min(beta, bestScore) : beta;

            if (beta <= alpha) {break;}
        }
        return bestScore;
    }

    public Point findBestMove(HexGameStatus status)
    {
        List<MoveNode> moveList = status.getMoves();
        int bestScore = Integer.MIN_VALUE;
        Point move = moveList.getFirst().getPoint();

        for (MoveNode mn : moveList) {
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());

            if (newStatus.isGameOver()) {
                return mn.getPoint();
            }

            int score = MinMax(newStatus, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, false);

            if (score > bestScore) {
                bestScore = score;
                move = mn.getPoint();
            }
        }

        return move;
    }
}
