

package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;

import java.awt.*;
import java.util.List;

/**
 * Implementació de l'algoritme MiniMax amb recerca iterativa per trobar la
 * millor jugada en el joc de Hex.
 */
public class Iterative extends MiniMax
{
    private int currentDepth;

	/**
	 * Constructor de la classe Iterative.
	 *
	 * @param maxDepth La profunditat màxima de cerca per l'algoritme.
	 */
    public Iterative(int maxDepth)
    {
        super(maxDepth);
    }

	/**
	 * Troba la millor jugada utilitzant una aproximació iterativa del MiniMax
	 * amb poda alpha-beta.
	 *
	 * @param status L'estat actual del joc de Hex.
	 * @return El punt del tauler corresponent a la millor jugada calculada.
	 */	
    @Override
    public Point findBestMove(HexGameStatus status)
    {
        exploratedNodes = 0;
        PlayerType player = status.getCurrentPlayer();
	List<MoveNode> moveList = status.getMoves();

	moveList.sort((a, b) -> {
            HexGameStatus statusA = new HexGameStatus(status);
            HexGameStatus statusB = new HexGameStatus(status);
            statusA.placeStone(a.getPoint());
            statusB.placeStone(b.getPoint());
            int scoreA = heuristic.evaluate(statusA, player);
            int scoreB = heuristic.evaluate(statusB, player);
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

                int score = getBestScore(newStatus, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, false, player);
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
	
	/**
	 * Atura l'execució de l'algoritme.
	 */
    @Override
    public void stop()
    {
        this.stop = true;
    }

	/**
	 * Obté el nombre total de nodes explorats fins al moment.
	 *
	 * @return El nombre de nodes explorats.
	 */	
    @Override
    public long getExplorationDepth()
    {
        return exploratedNodes;
    }
	
	/**
	 * Obté la profunditat màxima de cerca assolida durant la cerca iterativa.
	 *
	 * @return La profunditat màxima explorada.
	 */
    @Override
    public int getMaxDepth()
    {
        return currentDepth;
    }
}
