package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;

/**
 * Classe GameStatus que representa l'estat del joc Hex amb suport per al càlcul
 * eficient de hashes utilitzant Zobrist hashing.
 * 
 * Aquesta classe extén HexGameStatus, afegint funcionalitat per gestionar un 
 * hash únic que representa l'estat actual del joc, permetent així una gestió
 * eficient dels estats explorats.
 */
public class GameStatus extends HexGameStatus{

        /**
        * Hash únic que representa l'estat actual del tauler de joc, calculat mitjançant
        * el Zobrist hashing.
        */
	public long hash;
	
        /**
        * Constructor que crea un nou GameStatus a partir d'un estat de joc HexGameStatus
        * existent, inicialitzant el hash amb un valor donat.
        *
        * @param status       l'estat inicial del joc Hex.
        * @param zobristHash  el valor del hash Zobrist que representa l'estat inicial.
        */
	public GameStatus(HexGameStatus status, long zobristHash) {
		super(status);
		hash = zobristHash;
	}
	
        /**
        * Constructor que crea un nou GameStatus a partir d'un altre GameStatus existent.
        *
        * @param status l'estat de joc GameStatus a partir del qual es copiarà l'estat.
        */
	public GameStatus(GameStatus status) {
		super(status);
		this.hash = status.hash;
	}
	
	/**
        * Col·loca una pedra en una posició específica al tauler i actualitza el hash Zobrist
        * per reflectir el nou estat del joc.
        *
        * @param point         la posició al tauler on es col·locarà la pedra.
        * @param zobristTable  la taula de Zobrist utilitzada per actualitzar el hash.
        */
	public void placeStone(Point point, long[][][] zobristTable) {
		int player = super.currentPlayer == PlayerType.PLAYER1 ? 0 : 1;
		
		super.placeStone(point);
                
		// Actualitza el hash utilitzant el valor associat al jugador i la posició
		hash ^= zobristTable[player][point.x][point.y];
	}
	
}
