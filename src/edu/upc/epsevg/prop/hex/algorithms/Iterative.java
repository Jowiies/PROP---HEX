

package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;

import java.awt.*;
import java.util.HashMap;
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
    public Point findBestMove(GameStatus status)
    {
		zobristTable = buildZobristTable(status.getSize());
		transpositionTable = new HashMap();
		status.hash = calculateZobristHash(status);			// Calculamos la hash del tablero inicial
		
        exploratedNodes = 0;
		
        PlayerType player = status.getCurrentPlayer();
		
		List<MoveNode> moveList = sortedTrimedList(status);		// Devuelve la lista ordenada y recortada.
		Point bestInitialMove = null;
        Point move = moveList.getFirst().getPoint();
        Point trueMove = move;
		long bestHash = 0;
		
        for (int depth = 1; depth <= maxDepth && !stop; depth++) {

            int bestScore = Integer.MIN_VALUE;
	
            for (int i = 0; i < moveList.size(); ++i) {
                if (stop) {break;}
				MoveNode mn = moveList.get(i);

                GameStatus newStatus = new GameStatus(status);
				newStatus.placeStone(mn.getPoint(), zobristTable);

                if (newStatus.isGameOver()) {
                    return mn.getPoint();
                }

                int score = getBestScore(newStatus, depth-1, bestScore, Integer.MAX_VALUE, false, player);
                if (score > bestScore) {
                    bestScore = score;
                    move = mn.getPoint();
					bestHash = newStatus.hash;
                }
            }

            if (!stop) {
                currentDepth = depth;
                trueMove = move;
				
				if (maxDepth == 1) {
					MoveNode bestInitial = new MoveNode(move);
					moveList.remove(bestInitial);
					moveList.add(0,bestInitial);
					transpositionTable.put(bestHash, bestScore);
				} 
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
