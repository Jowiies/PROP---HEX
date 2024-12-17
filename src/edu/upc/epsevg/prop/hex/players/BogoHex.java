package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.algorithms.*;

public class BogoHex implements IPlayer, IAuto
{
    private final String name = "BogoHexPlayer";
    private boolean ids;
    MiniMax miniMaxAlgorithm;
    public BogoHex(int maxDepth, boolean ids)
    {
        miniMaxAlgorithm = ids ? new Iterative(maxDepth) : new Basic(maxDepth);
        this.ids = ids;
    }

    public BogoHex()
    {
        this.ids = true;
        miniMaxAlgorithm = new Iterative(Integer.MAX_VALUE);
    }

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

    @Override
    public void timeout()
    {
        miniMaxAlgorithm.stop();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

}
