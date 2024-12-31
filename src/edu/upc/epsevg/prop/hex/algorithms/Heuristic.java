package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Aquesta classe implementa diverses heurístiques i algoritmes per avaluar i
 * optimitzar moviments en un joc de Hex.
 */
public class Heuristic
{

    /**
	 * Avalua l'estat actual del joc segons diverses heurístiques.
	 *
	 * @param gameStatus L'estat actual del joc de Hex.
	 * @param player El jugador que es vol avaluar.
	 * @return Una puntuació heurística que representa la diferència entre les
	 * oportunitats del jugador actual i les de l'oponent.
	 */   
    public static int evaluate(HexGameStatus gameStatus, PlayerType player) {
        PlayerType opponent = PlayerType.opposite(player);
        int currentPlayerScore = dijkstra(gameStatus, player);
        //if(currentPlayerScore == 0) currentPlayerScore = Integer.MIN_VALUE;
        int opponentScore = dijkstra(gameStatus, opponent);
      //  if(opponentScore == 0) opponentScore = Integer.MIN_VALUE;
        //int color = (currentPlayer == PlayerType.PLAYER1) ? 1 : -1;
        

        return (opponentScore - currentPlayerScore);
				//+ evaluateConnectivity(gameStatus,gameStatus.getCurrentPlayerColor());
				// heuristicBlockOpponent(gameStatus,player) ;
				//- evaluateOpponentBarriers(gameStatus, gameStatus.getCurrentPlayerColor()) ;
    }
	
	/**
	 * Implementació de l'algoritme de Dijkstra per trobar el camí més curt
	 * entre els nodes d'un jugador específic en el tauler de Hex.
	 *
	 * @param game L'estat actual del joc de Hex.
	 * @param player El jugador per al qual s'executa l'algoritme.
	 * @return La distància més curta al llarg del tauler per al jugador
	 * especificat.
	 */	
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
			
			List<Point> neighbors = getVirtualNeighbors(currentPoint, game);
			for (Point neighbor : neighbors) {
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
	
	
	public static List<Point> getVirtualNeighbors(Point point, HexGameStatus game)
	{
		//System.out.println("Point:" + point);
		int[][] directions = {
			{-1,-1},	// Top Left 
			{1, -2},	// Top
			{2, -1},	// Top Right
			{1,  1},	// Bottom Right
			{-1, 2},	// Bottom
			{-2, 1}		// Bottom Left
		};
		
		int [][] neighbors = {
			{-1, 0},
			{0, -1},
			{1, -1},
			{1,  0},
			{0,  1},
			{-1, 1}
		};
				
		List<Point> virtualNeighbors = game.getNeigh(point);
		
		int size = game.getSize();
		int posX, posY;
		
		for (int i = 0; i < directions.length; ++i) {
			
			posX = point.x + directions[i][0];
			posY = point.y + directions[i][1];
			//System.out.println("Virtual Neighbor: (" + posX + "," + posY + ")");
			if (posX >= 0 && posX < size && posY >= 0 && posY < size) {
				//System.out.println("Im not out of bounds!");
				
				//int neigh1X = neighbors[i][0];
				//int neigh1Y = neighbors[i][1];
				//System.out.println("Neighbor1: (" + neigh1X + "," + neigh1Y + ")");
				
				//int neigh2X = neighbors[(i+1)%6][0];
				//int neigh2Y = neighbors[(i+1)%6][1];
				//System.out.println("Neighbor1: (" + neigh2X + "," + neigh2Y + ")");
				
				if (game.getPos(point.x + neighbors[i][0], point.y + neighbors[i][1]) == 0
					&& game.getPos(point.x + neighbors[(i+1)%6][0],point.y + neighbors[(i+1)%6][1]) == 0) 
				{
					virtualNeighbors.add(new Point(posX, posY));
				}
			}
			
		}
		
		return virtualNeighbors;
	}
	
	/**
	 * Avalua la connectivitat del tauler per al jugador donat.
	 *
	 * @param gameStatus L'estat actual del joc.
	 * @param player El color del jugador (1 o -1).
	 * @return Un valor que representa la força de les connexions del jugador en
	 * el tauler.
	 */    
    public static int evaluateConnectivity(HexGameStatus gameStatus, int player)
	{
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
	
	
	/**
	 * Heurística que avalua el bloqueig potencial de les jugades de l'oponent.
	 *
	 * @param gameStatus L'estat actual del joc.
	 * @param player El jugador actual.
	 * @return Un valor que representa l'eficàcia de bloquejar l'oponent.
	 */   
	public static int heuristicBlockOpponent(HexGameStatus gameStatus, PlayerType player)
	{
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
					if (blocksCriticalConnection(gameStatus, currentPoint, opponentColor)){
						blockScore += 10; // Ajusta este peso según la importancia.
					}
				}
			}
		}
		return blockScore;
	}
	
	/**
	 * Determina si un punt bloqueja una connexió crítica per a l'oponent.
	 *
	 * @param gameStatus L'estat actual del joc.
	 * @param point El punt a analitzar.
	 * @param opponentColor El color de l'oponent.
	 * @return Cert si el punt bloqueja una connexió crítica; fals en cas
	 * contrari.
	 */   
	public static boolean blocksCriticalConnection(HexGameStatus gameStatus, 
			Point point, int opponentColor) 
	{
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
	
	/**
	 * Avalua barreres creades per l'oponent que dificulten el progrés del
	 * jugador actual.
	 *
	 * @param gameStatus L'estat actual del joc.
	 * @param player El color del jugador actual (1 o -1).
	 * @return Un valor que representa la penalització per les barreres creades
	 * per l'oponent.
	 */    
    public static int evaluateOpponentBarriers(HexGameStatus gameStatus, int player)
	{
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
	
	/**
	 * Classe interna que representa un node en l'algoritme de Dijkstra. 
	 */
    private static class Node {
        Point point = null;
        int cost = 0;
		/**
		 * Constructor per inicialitzar un node amb un punt i un cost
		 *
		 * @param point El punt que representa aquest node.
		 * @param cost El cost associat a aquest punt.
		 */	
        Node(Point point, int cost) {
            this.point = point;
            this.cost = cost;
        }
    }

}
