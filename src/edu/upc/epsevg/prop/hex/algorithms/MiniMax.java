package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;

import java.awt.*;
import java.util.List;

public abstract class MiniMax
{
    protected boolean stop;
    protected int maxDepth;
    protected long exploratedNodes;
    protected final Heuristic heuristic = new Heuristic();

    public MiniMax(int maxDepth)
    {
        stop = false;
        this.maxDepth = maxDepth;
    }

    public abstract Point findBestMove(HexGameStatus status);
    public abstract void stop();
    public abstract long getExplorationDepth();
    public abstract int getMaxDepth();

    protected int getBestScore(HexGameStatus status, int depth, int alpha, int beta, boolean isMax, PlayerType player)
    {
        if (stop) {return 0;}
		
        if (depth == 0) {
            exploratedNodes++;     
            return heuristic.evaluate(status, isMax ? player : PlayerType.opposite(player));
        }
		
		List<MoveNode> moveList = status.getMoves();

		moveList.sort((a, b) -> {
                        PlayerType currentplayer = status.getCurrentPlayer();
			HexGameStatus statusA = new HexGameStatus(status);
			HexGameStatus statusB = new HexGameStatus(status);
			statusA.placeStone(a.getPoint());
			statusB.placeStone(b.getPoint());
			int scoreA = heuristic.evaluate(statusA, currentplayer);
			int scoreB = heuristic.evaluate(statusB, currentplayer);
			return Integer.compare(scoreB, scoreA); // Sort Descending
		});
		
		
        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (int i = 0; i < 20 && i < moveList.size(); ++i) {
            if (stop) {return 0;}
			
            MoveNode mn = moveList.get(i);
            HexGameStatus newStatus = new HexGameStatus(status);
            newStatus.placeStone(mn.getPoint());
			
            if (newStatus.isGameOver()) return isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			
            int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax, player);

            if (stop) {return 0;}

            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            if(isMax) alpha = Math.max(alpha, bestScore);
            else beta = Math.min(beta, bestScore);

            if (alpha >= beta) {break;}
        }

        return bestScore;
    }
}
