package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Heuristic
{
    public int getValue(HexGameStatus status)
    {
        return 0;
    }
    
    public static int evaluate(HexGameStatus gameStatus) {
        PlayerType currentPlayer = gameStatus.getCurrentPlayer();
        PlayerType opponent = PlayerType.opposite(currentPlayer);
        int currentPlayerScore = dijkstra(gameStatus, currentPlayer);
        int opponentScore = dijkstra(gameStatus, opponent);
        int color = (currentPlayer == PlayerType.PLAYER1) ? 1 : -1;


        return (currentPlayerScore - opponentScore);
				//+ evaluateConnectivity(gameStatus,color) 
				//+ heuristicBlockOpponent(gameStatus,currentPlayer) 
				//- evaluateOpponentBarriers(gameStatus, color) ;
   }
	public static int dijkstra(HexGameStatus game, PlayerType player) 
	{
		//System.out.println("This Is Dijkstra");
		int size = game.getSize();
		int[][] distance = new int[size][size];
		Point[][] predecessor = new Point[size][size];
		PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
		boolean[][] visited = new boolean[size][size]; 

		// Initialize distances with infinity 
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) { 
				distance[i][j] = Integer.MAX_VALUE;
				predecessor[i][j] = null;
			} 
		} 

		// Add virtual start nodes 
		if (player == PlayerType.PLAYER2) { 
			for (int x = 0; x < size; x++) {
				//if (game.getPos(x,0) == -1) {
					distance[x][0] = 0; 
					pq.add(new Node(new Point(x, 0), 0));
				//}
				//else if (game.getPos(x,0) == 0) {
				//	distance[x][0] = 1; 
				//	pq.add(new Node(new Point(x, 0), 0));
				//}
				 
			} 
		} else { 
			for (int y = 0; y < size; y++) { 
				//if (game.getPos(0,y) == -1) {
					distance[0][y] = 0; 
					pq.add(new Node(new Point(0, y), 0));
				//}
				//else if (game.getPos(0,y) == 0) {
				//	distance[0][y] = 1; 
				//	pq.add(new Node(new Point(0, y), 0));
				//} 
			} 
		} 
		// Dijkstra's algorithm 
		while (!pq.isEmpty()) { 
			Node current = pq.poll(); 
			Point currentPoint = current.point;
			
			if (visited[currentPoint.x][currentPoint.y]) continue;
			visited[currentPoint.x][currentPoint.y] = true;
			//System.out.println("NEIGHBORS:");
			
			for (Point neighbor : game.getNeigh(currentPoint)) {
				//System.out.println(neighbor);
				
				if (visited[neighbor.x][neighbor.y]) continue;
				
				int newDist = distance[currentPoint.x][currentPoint.y]; 
				if (game.getPos(neighbor) == 0) {
					newDist++;
				} else if (game.getPos(neighbor) != PlayerType.getColor(player)) {
					continue;
				}
				
				if (newDist < distance[neighbor.x][neighbor.y]) { 
					distance[neighbor.x][neighbor.y] = newDist; 
					predecessor[neighbor.x][neighbor.y] = currentPoint; 
					pq.add(new Node(neighbor, newDist)); 
				} 
			} 
		}
		
		// Find the shortest distance to the virtual end nodes 
		int shortestDist = Integer.MAX_VALUE; 
		if (player == PlayerType.PLAYER2) { 
			for (int x = 0; x < size; x++) { 
				shortestDist = Math.min(shortestDist, distance[x][size - 1]); 
			} 
		} else { 
			for (int y = 0; y < size; y++) { 
				shortestDist = Math.min(shortestDist, distance[size - 1][y]); 
			} 
		} 
		
		return shortestDist;
	}
    
    public static int evaluateConnectivity(HexGameStatus gameStatus, int player) {
        int score = 0;
        
        // Contar cuántos caminos abiertos tiene el jugador (esto depende de tu implementación de la conectividad)
        for (int x = 0; x < gameStatus.getSize(); x++) {
            for (int y = 0; y < gameStatus.getSize(); y++) {
                if (gameStatus.getPos(x, y) == player) {
                    List<Point> neighbors = gameStatus.getNeigh(new Point(x, y));
                    for (Point neighbor : neighbors) {
                        if (gameStatus.getPos(neighbor.x, neighbor.y) == player) {
                            score++; // Un camino abierto
                        }
                    }
                }
            }
        }
        return score;
    }
    
    private static int heuristicBlockOpponent(HexGameStatus gameStatus, PlayerType player) {
    int n = gameStatus.getSize();
    int blockScore = 0;

    // Determina el jugador contrario
    PlayerType opponent = (player == PlayerType.PLAYER1) ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
    int color = (opponent == PlayerType.PLAYER1) ? 1 : -1;
    // Recorre todo el tablero
    for (int x = 0; x < n; x++) {
        for (int y = 0; y < n; y++) {
            Point currentPoint = new Point(x, y);

            // Si la celda está vacía y tiene algún vecino del oponente, evalúa la oportunidad de bloquear
            if (gameStatus.getPos(currentPoint) == 0) {
                List<Point> neighbors = gameStatus.getNeigh(currentPoint);

                // Verifica si alguna de las celdas vecinas está ocupada por el oponente
                boolean canBlock = false;
                for (Point neighbor : neighbors) {
                    if (gameStatus.getPos(neighbor) == color) {
                        canBlock = true;
                        break;
                    }
                }

                // Si hay un vecino del oponente, aumenta la puntuación para bloquear
                if (canBlock) {
                    blockScore += 2;  // Penaliza el movimiento enemigo
                }
            }
        }
    }

    return blockScore;
}
    
        private static int evaluateOpponentBarriers(HexGameStatus gameStatus, int player) {
        int opponent = (player == 1) ? -1 : 1;
        int score = 0;
        
        // Buscar bloqueos del oponente (jugadas que bloquean el avance)
        for (int x = 0; x < gameStatus.getSize(); x++) {
            for (int y = 0; y < gameStatus.getSize(); y++) {
                if (gameStatus.getPos(x, y) == opponent) {
                    List<Point> neighbors = gameStatus.getNeigh(new Point(x, y));
                    for (Point neighbor : neighbors) {
                        if (gameStatus.getPos(neighbor.x, neighbor.y) == 0) {
                            score--; // Penalizamos las barreras
                        }
                    }
                }
            }
        }
        return score;
    }
    
        private static class Node {
        Point point = null;
        int cost = 0;

        Node(Point point, int cost) {
            this.point = point;
            this.cost = cost;
        }
    }

}
