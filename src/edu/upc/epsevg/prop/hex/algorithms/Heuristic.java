package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.List;
import java.util.PriorityQueue;

public class Heuristic
{
    
    public static int evaluate(HexGameStatus gameStatus, PlayerType player) {
        PlayerType opponent = PlayerType.opposite(player);
        int currentPlayerScore = dijkstra(gameStatus, player);
        //if(currentPlayerScore == 0) currentPlayerScore = Integer.MIN_VALUE;
        int opponentScore = dijkstra(gameStatus, opponent);
      //  if(opponentScore == 0) opponentScore = Integer.MIN_VALUE;
        //int color = (currentPlayer == PlayerType.PLAYER1) ? 1 : -1;
        

        return (opponentScore - currentPlayerScore) ;
				//+ evaluateConnectivity(gameStatus,color)
				//+ heuristicBlockOpponent(gameStatus,player) ;
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
				
				distance[x][0] = game.getPos(x,0) == 0 ? 1 : 0; 
				pq.add(new Node(new Point(x, 0), 0));
			} 
		} else { 
			for (int y = 0; y < size; y++) { 
				distance[0][y] = game.getPos(0,y) == 0 ? 1 : 0; 
				pq.add(new Node(new Point(0, y), 0));
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
		Point endPoint = null;
		
		if (player == PlayerType.PLAYER2) {
			for (int x = 0; x < size; x++) {
				if (distance[x][size - 1] < shortestDist) {
					shortestDist = distance[x][size - 1];
					endPoint = new Point(x, size - 1);
				}
			}
		} else {
			for (int y = 0; y < size; y++) {
				if (distance[size - 1][y] < shortestDist) {
					shortestDist = distance[size - 1][y];
					endPoint = new Point(size - 1, y);
				}
			}
		}
		/*
		System.out.println("End of the path...");
		while (endPoint != null) {
			System.out.println(endPoint);
			endPoint = predecessor[endPoint.x][endPoint.y];
		}
		System.out.println("Begining of the path...");
		*/
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
    int opponentColor = (opponent == PlayerType.PLAYER1) ? 1 : -1;

    // Recorre todo el tablero
    for (int x = 0; x < n; x++) {
        for (int y = 0; y < n; y++) {
            Point currentPoint = new Point(x, y);

            // Si la celda está vacía, evalúa su valor estratégico
            if (gameStatus.getPos(currentPoint) == 0) {
                List<Point> neighbors = gameStatus.getNeigh(currentPoint);
                int opponentNeighbors = 0;

                // Cuenta vecinos ocupados por el oponente
                for (Point neighbor : neighbors) {
                    if (gameStatus.getPos(neighbor) == opponentColor) {
                        opponentNeighbors++;
                    }
                }

                // Aumenta el puntaje según la cantidad de vecinos enemigos
                if (opponentNeighbors > 0) {
                    blockScore += opponentNeighbors * 3; // Penaliza más si hay más vecinos enemigos.
                }

                // Penaliza aún más si el punto bloquea una conexión directa
                if (blocksCriticalConnection(gameStatus, currentPoint, opponentColor)) {
                    blockScore += 10; // Ajusta este peso según la importancia.
                }
            }
        }
    }

    return blockScore;
}
    
    private static boolean blocksCriticalConnection(HexGameStatus gameStatus, Point point, int opponentColor) {
    List<Point> neighbors = gameStatus.getNeigh(point);
    int connectedNeighbors = 0;

    // Verifica cuántos vecinos están ocupados por el oponente
    for (Point neighbor : neighbors) {
        if (gameStatus.getPos(neighbor) == opponentColor) {
            connectedNeighbors++;
        }
    }

    // Considera crítico si conecta dos o más piezas del oponente
    return connectedNeighbors > 1;
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
