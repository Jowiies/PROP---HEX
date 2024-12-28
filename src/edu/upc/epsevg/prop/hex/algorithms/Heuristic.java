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
        PlayerType opponent = (currentPlayer == PlayerType.PLAYER1) ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
        int currentPlayerScore = dijkstra(gameStatus, currentPlayer);
        int opponentScore = dijkstra(gameStatus, opponent);
        int color = (currentPlayer == PlayerType.PLAYER1) ? 1 : -1;


        return (opponentScore-currentPlayerScore) + evaluateConnectivity(gameStatus,color) + heuristicBlockOpponent(gameStatus,currentPlayer) - evaluateOpponentBarriers(gameStatus, color) ;
   }

        public static int dijkstra(HexGameStatus gameStatus, PlayerType player) {
        int n = gameStatus.getSize();
        Map<Point, Integer> distances = new HashMap<>();
        PriorityQueue<Point> pq = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        Set<Point> visited = new HashSet<>();
        // Nodo virtual inicial y final
        Point startNode = new Point(0, 0);
        Point endNode = new Point(-2, -2);

        // Inicializar la cola de prioridad con nodos virtuales
        for (int i = 0; i < n; i++) {
            Point initialPoint = (player == PlayerType.PLAYER1) ? new Point(i, 0) : new Point(0, i);
            distances.put(initialPoint, Integer.MAX_VALUE);
           // pq.add(initialPoint);
            
            //System.out.println(initialPoint);
           // Point finalPoint = (player == PlayerType.PLAYER1) ? new Point(i, n - 1) : new Point(n - 1, i);
            //distances.put(finalPoint, Integer.MAX_VALUE);
        }
        distances.put(startNode, 0);
        distances.put(endNode, Integer.MAX_VALUE);
        pq.add(startNode);
       // visited.add(startNode); 

        while (!pq.isEmpty()) {
            Point current = pq.poll();
           // System.out.println(pq.peek() + " " + pq.peek() + "nei");
            //
            if (current.equals(endNode)) {
                return distances.get(current);
            }

            if (visited.contains(current)) {
                continue;
            }

            int k = 0;
            for (Point neighbor : gameStatus.getNeigh(current)) {
                
                if (visited.contains(neighbor)) {
                    continue;
                }
                

                int cellState = gameStatus.getPos(neighbor);
                int cost;
                int color = (player == PlayerType.PLAYER1) ? 1 : -1;
                if (cellState == color ) {
                    cost = 0; // Nodo del jugador actual
                } else if (cellState == 0) {
                    cost = 1; // Nodo vacío
                } else {
                    continue; // Nodo ocupado por el oponente, no se puede atravesar
                }
                
                boolean helpsAdvance = (player == PlayerType.PLAYER1)? neighbor.x > current.x // Avanza hacia la meta en x
                : neighbor.y > current.y; // Avanza hacia la meta en y

                if (!helpsAdvance) {
                    cost += 2; // Penalización adicional para nodos que no ayudan
                }

                int newDist = newDist = distances.get(current) + cost ;;
               // if(distances.get(current)== Integer.MAX_VALUE ){
                //    newDist = cost;
               // }else 
                

                if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDist);
                  
                    pq.add(neighbor);
                     
                    //System.out.println(neighbor + " " + distances.get(neighbor));
                }
                
            }
           if (player == PlayerType.PLAYER1 && current.x == n - 1 ||
            player == PlayerType.PLAYER2 && current.y == n - 1) {
            if (distances.get(current) < distances.getOrDefault(endNode, Integer.MAX_VALUE)) {
                distances.put(endNode, distances.get(current));
                pq.add(endNode);
            }
        }
         visited.add(current);    
        }

        return Integer.MAX_VALUE; // No se encontró un camino
}
        
    
    public static int evaluateConnectivity(HexGameStatus gameStatus, int player) {
        int score = 0;
        
        // Contar cuántos caminos abiertos tiene el jugador (esto depende de tu implementación de la conectividad)
        for (int x = 0; x < gameStatus.getSize(); x++) {
            for (int y = 0; y < gameStatus.getSize(); y++) {
                if (gameStatus.getPos(x, y) == player) {
                    List<Point> neighbors = gameStatus.getNeigh(new Point(x, y));
                    for (Point neighbor : neighbors) {
                        if (gameStatus.getPos(neighbor.x, neighbor.y) == 0) {
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
