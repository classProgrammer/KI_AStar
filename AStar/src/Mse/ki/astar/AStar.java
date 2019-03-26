package Mse.ki.astar;

import Mse.ki.heuristic.IHeuristic;

import java.util.*;

class Node implements Comparable<Node> {

    public Node getNext()  {
        return null;
    }

    public int getValue()  {
        return 0;
    }

    public Map<Node, Integer> getDistances() {
        return null;
    }

    @Override
    public int compareTo(Node o) {
        return 0;
    }
}

public class AStar {

    private ArrayList<Node> nodes;
    private IHeuristic<Integer, Node> heuristic;

    private List<Node> reconstructPath(Map<Node, Node> cameFrom, Node current) {
        var totalPath = new ArrayList<Node>();
        while(cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        return totalPath;
    }

    public List<Node> A_Star(Node start, Node goal) {
        var closedSet = new TreeSet<Node>();
        var openSet = new TreeSet<Node>();

        openSet.add(start);

        var cameFrom = new HashMap<Node, Node>();
        var gScore = new HashMap<Node, Integer>();
        var fScore = new HashMap<Node, Integer>();

        for (var node : nodes) {
            gScore.put(node, Integer.MAX_VALUE);
            fScore.put(node, Integer.MAX_VALUE);
        }
        gScore.put(start, 0);

        fScore.put(start, heuristic.estimate(start, goal));

        while (!openSet.isEmpty()) {
            var current = openSet.first();

            if (current == goal) {
                return reconstructPath(cameFrom, current);
            }

            openSet.remove(current);
            closedSet.add(current);

            for(var neighbor: current.getDistances().keySet()) {
                if (!closedSet.contains(neighbor)) {
                    var tentative_gScore = gScore.get(current) + current.getDistances().get(neighbor);

                    if (openSet.contains(neighbor)) {
                        cameFrom.put(neighbor, current);
                        gScore.put(neighbor, tentative_gScore);
                        fScore.put(neighbor, gScore.get(neighbor) + heuristic.estimate(neighbor, goal));
                    }
                }
            }
        }
        return null;
    }
}
