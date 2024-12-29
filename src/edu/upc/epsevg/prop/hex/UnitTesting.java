/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.algorithms.Basic;
import edu.upc.epsevg.prop.hex.algorithms.Heuristic;
import edu.upc.epsevg.prop.hex.algorithms.Iterative;
import edu.upc.epsevg.prop.hex.algorithms.MiniMax;
import edu.upc.epsevg.prop.hex.players.BogoHex;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus2;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3.Result;
import java.awt.Point;
/**
 *
 * @author bernat
 */
public class UnitTesting {
    
    public static void main(String[] args) {
    
        
        byte[][] board = {
        //X   0  1  2  3  4  5  6  7  8
            { 0, 0, 0, 0, 0, 0, 0, 0, -1},                     // 0   Y
              { 0, 0, 0, 0, 0, 0, 0,-1, 0},                    // 1
                { 0, 0, 0, 0, 0, 0, 0,-1, 0},                  // 2
                  { 0, 0, 0, 0, 0, 0, 0,-1, 0},                // 3
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0},              // 4  
                      { 0, 0, 0, 0, 0, 0,-1, 0, 0},            // 5    
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0},          // 6      
                          { 0, 0, 0, 0, 0,-1, 0, 0, 0},        // 7       
                            { 0, 0, 0, 0, 0, 0, 0, 0, 0}       // 8    Y         
        };
       HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1); 
        // Probar el algoritmo de Dijkstra
		System.out.println("=== Test de Dijkstra ===");
			
        int player1Cost = Heuristic.dijkstra(gs, PlayerType.PLAYER1);
		System.out.println(
				"Coste para PLAYER1(" 
				+ PlayerType.getColor(PlayerType.PLAYER1)
				+"): " + player1Cost
		);
		
		int player2Cost = Heuristic.dijkstra(gs, PlayerType.PLAYER2);
		System.out.println(
				"Coste para PLAYER2(" 
				+ PlayerType.getColor(PlayerType.PLAYER2)
				+"): " + player2Cost
		);
		
		int eval = Heuristic.evaluate(gs);
		System.out.println(
				"Evaluation: "
				+ eval
		);
		
		/*
		MiniMax miniMaxAlgorithm = new Basic(5);
	    Point bestMove1 = miniMaxAlgorithm.findBestMove(gs);
		System.out.println(
				"Movimiento para PLAYER1(" 
				+ PlayerType.getColor(PlayerType.PLAYER1)
				+"): " + bestMove1
		);
		*/
	//System.out.println("Coste para PLAYER2:(" + 
	//			PlayerType.getColor(PlayerType.PLAYER2) +"): " + player2Cost);

    }  
}
