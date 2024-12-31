package edu.upc.epsevg.prop.hex.algorithms;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.PlayerType;
import java.awt.Point;
import java.util.Arrays;
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
        int opponentScore = dijkstra(gameStatus, opponent);

        return (opponentScore - currentPlayerScore);
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
	int size = game.getSize();
	int[][] distance = new int[size][size];
	boolean[][] visited = new boolean[size][size]; 
	PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.cost - b.cost);
        for (int[] row : distance) Arrays.fill(row, Integer.MAX_VALUE);
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
			
            List<Point> neighbors = getVirtualNeighbors(currentPoint, game);
            for (Point neighbor : neighbors) {
                if (visited[neighbor.x][neighbor.y]) continue;		
		int newDist = distance[currentPoint.x][currentPoint.y]; 
		if (game.getPos(neighbor) == 0) {
                    newDist++;
                }else if (game.getPos(neighbor) != PlayerType.getColor(player)) {
                    continue;
		}
                if (newDist < distance[neighbor.x][neighbor.y]) { 
                    distance[neighbor.x][neighbor.y] = newDist; 
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
	
	/**
        * Retorna una llista de veïns virtuals per a un punt donat en el tauler del joc Hex.
        *
        * Els veïns virtuals són posicions que no estan connectades directament al punt actual,
        * però que es poden considerar com a possibles connexions basades en la proximitat
        * i les condicions del tauler.
        *
        * @param point el punt actual del qual es volen calcular els veïns virtuals.
        * @param game l'estat actual del joc Hex, que inclou informació del tauler i les seves posicions.
        * @return una llista de veïns virtuals calculats per al punt donat.
         */
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
			
			if (posX >= 0 && posX < size && posY >= 0 && posY < size) {
	
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
