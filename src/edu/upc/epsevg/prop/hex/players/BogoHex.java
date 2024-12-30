package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.algorithms.*;

/**
 * Implementació del jugador BogoHex per al joc de Hex.
 * Aquest jugador utilitza un algoritme MiniMax amb o sense Iterative Deepening Search (IDS).
 */
public class BogoHex implements IPlayer, IAuto
{
    private final String name = "BogoHexPlayer";
    private boolean ids;
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

}
