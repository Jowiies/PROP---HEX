package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.*;
import edu.upc.epsevg.prop.hex.algorithms.*;

public class BogoHex implements IPlayer, IAuto
{
    private final String name = "BogoHexPlayer";
    private boolean ids;
    MinMax minMaxAlgorithm;
    public BogoHex(int maxDepth, boolean ids)
    {
        minMaxAlgorithm = ids ? new Iterative(maxDepth) : new Basic(maxDepth);
        this.ids = ids;
    }

    @Override
    public PlayerMove move(HexGameStatus hexGameStatus)
    {
        return new PlayerMove(
                minMaxAlgorithm.findBestMove(hexGameStatus),
                minMaxAlgorithm.getExplorationDepth(),
                minMaxAlgorithm.getMaxDepth(),
                ids ? SearchType.MINIMAX_IDS : SearchType.MINIMAX
        );
    }

    @Override
    public void timeout()
    {
        minMaxAlgorithm.stop();
    }

    @Override
    public String getName()
    {
        return this.name;
    }

}
