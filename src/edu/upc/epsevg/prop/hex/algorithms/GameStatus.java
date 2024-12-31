package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;

public class GameStatus extends HexGameStatus{

	public long hash;
	
	public GameStatus(HexGameStatus status, long zobristHash) {
		super(status);
		hash = zobristHash;
	}
	
	public GameStatus(GameStatus status) {
		super(status);
		this.hash = status.hash;
	}
	
	
	public void placeStone(Point point, long[][][] zobristTable) {
		int player = super.currentPlayer == PlayerType.PLAYER1 ? 0 : 1;
		
		super.placeStone(point);
		
		hash ^= zobristTable[player][point.x][point.y];
	}
	
}
