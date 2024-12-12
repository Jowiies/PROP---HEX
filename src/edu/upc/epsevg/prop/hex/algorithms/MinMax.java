package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.algorithms.Heuristic;

import java.awt.*;
import java.util.List;

public abstract class MinMax
{
    protected boolean stop;
    protected int maxDepth;
    protected long exploratedNodes;
    private final Heuristic heuristic = new Heuristic();

    public MinMax(int maxDepth)
    {
        this.maxDepth = maxDepth;
    }

    public abstract Point findBestMove(HexGameStatus status);
    public abstract void stop();
    public abstract long getExplorationDepth();
    public abstract int getMaxDepth();

}
