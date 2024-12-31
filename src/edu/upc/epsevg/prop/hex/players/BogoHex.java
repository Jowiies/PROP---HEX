package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.algorithms.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Implementació del jugador BogoHex per al joc de Hex.
 * Aquest jugador utilitza un algoritme MiniMax amb o sense Iterative Deepening Search (IDS).
 */
public class BogoHex implements IPlayer, IAuto
{
    private final String name = "BogoHexPlayer";
    private boolean ids;
    private static final int BOARD_SIZE = 7; //luego mirar
    private static final long[][][] zobristTable = buildZobristTable();
    private static final Map<Long, Integer> transpositionTable = new HashMap<>();
    MiniMax miniMaxAlgorithm;
	
	/**
     * Constructor de la classe BogoHex amb configuració personalitzada.
     *
     * @param maxDepth La profunditat màxima de cerca.
     * @param ids Indica si s'utilitza Iterative Deepening Search (IDS).
     */
    public BogoHex(int maxDepth, boolean ids)
    {
        miniMaxAlgorithm = ids ? new Iterative(maxDepth) : new Basic(maxDepth);
        this.ids = ids;
    }

	/**
	 * Constructor per defecte de la classe BogoHex. Utilitza Iterative
	 * Deepening Search (IDS) amb una profunditat màxima infinita.
	 */
    public BogoHex()
    {
        this.ids = true;
        miniMaxAlgorithm = new Iterative(Integer.MAX_VALUE);
    }

	/**
	 * Genera el següent moviment del jugador utilitzant l'algoritme MiniMax.
	 *
	 * @param hexGameStatus L'estat actual del joc de Hex.
	 * @return Un objecte {@link PlayerMove} que conté el moviment calculat i
	 * informació sobre la cerca (nodes explorats, profunditat màxima i tipus de
	 * cerca).
	 */
    @Override
    public PlayerMove move(HexGameStatus hexGameStatus)
    {
        long zobristHash = calculateZobristHash(hexGameStatus); //-- > le pasamos el tablero del juego, para calcular el hash principal
        //int score = minimax(board, zobristHash, 3, true);
        return new PlayerMove(
                miniMaxAlgorithm.findBestMove(hexGameStatus),
                miniMaxAlgorithm.getExplorationDepth(),
                miniMaxAlgorithm.getMaxDepth(),
                ids ? SearchType.MINIMAX_IDS : SearchType.MINIMAX
        );
    }

	/**
	 * Atura l'algoritme MiniMax en cas de timeout.
	 */
    @Override
    public void timeout()
    {
        miniMaxAlgorithm.stop();
    }

	/**
	 * Retorna el nom del jugador.
	 *
	 * @return El nom del jugador.
	 */
    @Override
    public String getName()
    {
        return this.name;
    }
    
    public static long[][][] buildZobristTable() {
        long[][][] table = new long[2][BOARD_SIZE][BOARD_SIZE];
        Random random = new Random();
        for (int player = 0; player < 2; player++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    table[player][x][y] = random.nextLong();
                }
            }
        }
        return table;
    }

    public static long calculateZobristHash(HexGameStatus gs) {
        long hash = 0L;
        for (int x = 0; x < gs.getSize(); x++) {
            for (int y = 0; y < gs.getSize(); y++) {
                if (gs.getPos(x, y) != 0) {
                    hash ^= zobristTable[gs.getPos(x, y)- 1][x][y];
                }
            }
        }
        return hash;
    }

}
