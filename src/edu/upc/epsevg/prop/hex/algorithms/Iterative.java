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

		moveList.sort((a, b) -> {
			HexGameStatus statusA = new HexGameStatus(status);
			HexGameStatus statusB = new HexGameStatus(status);
			statusA.placeStone(a.getPoint());
			statusB.placeStone(b.getPoint());
			int scoreA = heuristic.evaluate(statusA);
			int scoreB = heuristic.evaluate(statusB);
			return Integer.compare(scoreB, scoreA); // Sort Descending
		});
		
        Point move = moveList.getFirst().getPoint();
        Point trueMove = move;

        for (int depth = 1; depth <= maxDepth && !stop; depth++) {

            int bestScore = Integer.MIN_VALUE;

            for (int i = 0; i < 20 && i < moveList.size(); ++i) {
                if (stop) {break;}
				MoveNode mn = moveList.get(i);

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
