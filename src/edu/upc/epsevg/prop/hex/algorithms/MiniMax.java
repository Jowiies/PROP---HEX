

package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerType;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Classe abstracta que implementa l'algoritme MiniMax per al joc de Hex.
 * Proporciona funcionalitats bàsiques de MiniMax amb poda alfa-beta i ordenació
 * de moviments basada en heurístiques.
 */
public abstract class MiniMax
{
    protected boolean stop;
    protected int maxDepth;
    protected long exploratedNodes;
	protected static long[][][] zobristTable = null;
	protected static Map<Long, Integer> transpositionTable = null;
	 
	/**
	 * Constructor de la classe MiniMax.
	 *
	 * @param maxDepth La profunditat màxima de cerca.
	 */
    public MiniMax(int maxDepth)
    {
        stop = false;
        this.maxDepth = maxDepth;
    }

    public abstract Point findBestMove(GameStatus status);
    public abstract void stop();
    public abstract long getExplorationDepth();
    public abstract int getMaxDepth();

	/**
	 * Calcula la millor puntuació per a un estat donat del joc utilitzant
	 * l'algoritme MiniMax amb poda alfa-beta.
	 *
	 * @param status L'estat actual del joc.
	 * @param depth La profunditat restant de la cerca.
	 * @param alpha El valor alfa per a la poda alfa-beta.
	 * @param beta El valor beta per a la poda alfa-beta.
	 * @param isMax Indica si és el torn del jugador maximitzador.
	 * @param player El nostre jugador.
	 * 
	 * @return La millor puntuació calculada per a l'estat donat.
	 */
    protected int getBestScore(GameStatus status, int depth, int alpha, int beta, boolean isMax, PlayerType player)
    {
        if (status.isGameOver()) return isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        if (stop) {return 0;}
		
        if (depth == 0) {
            exploratedNodes++;     
            return Heuristic.evaluate(status, /*isMax ?*/ player /*: PlayerType.opposite(player)*/);
        }
        
        
		
	List<MoveNode> moveList = sortedTrimedList(status);
	
        if (zobristTable != null) {
            GameStatus bestStatus = getBestBoard(status);
            if (bestStatus != null) return getBestScore(bestStatus, depth - 1, alpha, beta, !isMax, player);
        }
		
        int bestScore = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		
	long bestBoardH = 0;
        for (int i = 0; i < moveList.size(); ++i) {
            if (stop) {return 0;}
			
			if (isMax) {
                            MoveNode mn = moveList.get(i);
                            GameStatus newStatus = new GameStatus(status);
                            if (zobristTable != null ) newStatus.placeStone(mn.getPoint(), zobristTable);
                            else newStatus.placeStone(mn.getPoint());	

			
                            if (newStatus.isGameOver()) return isMax ? Integer.MAX_VALUE : Integer.MIN_VALUE;
			
                                int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax, player);
                                if (stop) {return 0;}
				if (score > bestScore) {
					bestScore = score;
					bestBoardH = newStatus.hash;
				}
				alpha = Math.max(alpha, bestScore);
			}
			else {
                            MoveNode mn = moveList.get(i);
                            GameStatus newStatus = new GameStatus(status);
                            if (zobristTable != null ) newStatus.placeStone(mn.getPoint(), zobristTable);
                            else newStatus.placeStone(mn.getPoint());	
			
                            int score = getBestScore(newStatus, depth - 1, alpha, beta, !isMax, player);
                            if (stop) {return 0;}
                            if (score < bestScore) {
				bestScore = score;
				bestBoardH = newStatus.hash;
                            }
                            beta = Math.min(beta, bestScore);
			}
			/*
            bestScore = isMax ? Math.max(bestScore, score) : Math.min(bestScore, score);

            if(isMax) alpha = Math.max(alpha, bestScore);
            else beta = Math.min(beta, bestScore);
			*/
            if (alpha >= beta) {break;}
        }
	
        if (zobristTable != null) transpositionTable.put(bestBoardH, bestScore);
        return bestScore;
    }
	
	protected static long[][][] buildZobristTable(int size) {
		
        long[][][] table = new long[2][size][size];
        Random random = new Random();
        for (int player = 0; player < 2; player++) {
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    table[player][x][y] = random.nextLong();
                }
            }
        }
        return table;
    }

    protected static long calculateZobristHash(HexGameStatus gs) {
        long hash = 0L;
        for (int x = 0; x < gs.getSize(); x++) {
            for (int y = 0; y < gs.getSize(); y++) {
                if (gs.getPos(x, y) != 0) {
					int playerId = gs.getPos(x,y) == 1 ? 0 : 1;
                    hash ^= zobristTable[playerId][x][y];
                }
            }
        }
        return hash;
    }
	
	protected GameStatus getBestBoard(GameStatus status) 
	{
		for (MoveNode mn : status.getMoves()) {
			GameStatus newStatus = new GameStatus(status);
			newStatus.placeStone(mn.getPoint(),zobristTable);
			if (transpositionTable.containsKey(newStatus.hash))
				return newStatus;
		}
		return null;
	}
	
	protected List<MoveNode> sortedTrimedList(GameStatus status) 
	{
		PlayerType player = status.getCurrentPlayer();
		List<MoveNode> moveList = status.getMoves();
		moveList.sort((a, b) -> {
			GameStatus statusA = new GameStatus(status);
			GameStatus statusB = new GameStatus(status);
			statusA.placeStone(a.getPoint());
			statusB.placeStone(b.getPoint());
			int scoreA = Heuristic.evaluate(statusA, player);
			int scoreB = Heuristic.evaluate(statusB, player);
			return Integer.compare(scoreB, scoreA); // Sort Descending
		});
		
		if (moveList.size() > 20) moveList = moveList.subList(0,20);
		/*
		for (int i = 0; i < moveList.size(); ++i) {
			MoveNode mn = moveList.get(i);
			GameStatus newStatus = new GameStatus(status);
			newStatus.placeStone(mn.getPoint(), zobristTable);		// Ponemos una piedra y actualizamos la hash
			if (transpositionTable.containsKey(newStatus.hash)) {
				// Si está en la tabla, pone el movimiento al principio de la lista y acaba el bucle
				moveList.remove(mn);
				moveList.add(0, mn);
				break;
			}
		}
		*/
		return moveList;
	}
}
