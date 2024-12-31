

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
            return Heuristic.evaluate(status, player );
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

            if (alpha >= beta) {break;}
        }
	
        if (zobristTable != null) transpositionTable.put(bestBoardH, bestScore);
        return bestScore;
    }
	/**
        * Construeix una taula de Zobrist per a un tauler de la mida especificada.
        * La taula de Zobrist és una estructura que conté valors aleatoris únics 
        * per a cada combinació de jugador i posició al tauler.
        * S'utilitza per calcular hashes únics d'estats de joc en algoritmes com Minimax.
        *
        * @param size la mida del tauler (nombre de files i columnes).
        * @return una taula tridimensional de nombres aleatoris, on l'índex és:
        *         [jugador][posició x][posició y].
        */
    public static long[][][] buildZobristTable(int size) {
		
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

    /**
    * Calcula el hash Zobrist per a un estat del joc Hex.
    * Aquest hash es genera utilitzant la taula de Zobrist i representa 
    * un identificador únic per a l'estat actual del tauler.
    *
    * @param gs l'estat actual del joc Hex.
    * @return un valor hash únic que representa l'estat actual del joc.
    */
    public static long calculateZobristHash(HexGameStatus gs) {
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

    /**
    * Obté el millor estat del tauler a partir dels moviments disponibles 
    * utilitzant la taula de transposició. Si un estat ja està emmagatzemat a 
    * la taula de transposició, es retorna aquest estat com el millor tauler.
    *
    * @param status l'estat actual del joc, incloent la llista de moviments disponibles.
    * @return el millor estat del joc trobat a la taula de transposició, 
    *         o null si no se'n troba cap.
    */
    public GameStatus getBestBoard(GameStatus status) 
    {
        for (MoveNode mn : status.getMoves()) {
                GameStatus newStatus = new GameStatus(status);
                newStatus.placeStone(mn.getPoint(),zobristTable);
                if (transpositionTable.containsKey(newStatus.hash))
                        return newStatus;
        }
        return null;
    }
    
    /**
    * Genera una llista ordenada i retallada dels moviments disponibles al tauler.
    * Els moviments es classifiquen en funció de la seva puntuació heurística, 
    * calculada per al jugador actual. Després d'ordenar, la llista es retalla per 
    * incloure només els 20 millors moviments.
    *
    * @param status l'estat actual del joc, incloent la llista de moviments disponibles.
    * @return una llista dels 20 millors moviments ordenats per puntuació heurística.
    */
    public List<MoveNode> sortedTrimedList(GameStatus status) 
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
        return moveList;
    }
}
