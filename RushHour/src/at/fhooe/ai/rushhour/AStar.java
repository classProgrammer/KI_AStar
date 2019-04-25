package at.fhooe.ai.rushhour;

import java.util.*;

public class AStar {

    /** The solution path is stored here */
    public State[] path;

    private void reconstructPath(Map<Node, Node> cameFrom, Node current) {
        var totalPath = new ArrayList<State>();
        while(cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(0, current.getState());
        }
        path = new State[totalPath.size()];
        path = totalPath.toArray(path);
    }

    private static final int INFINITY = Integer.MAX_VALUE;

    public AStar(Puzzle puzzle, Heuristic heuristic) {
        // save "cheapest paths"
        var cameFrom = new HashMap<Node, Node>();
        // cost from start node to the current node
        var gScore = new HashMap<Node, Integer>();
        // start node
        var start = puzzle.getInitNode();
        // open and closed sets
        var openSet = new PriorityQueue<Node>();
        var closedSet = new PriorityQueue<Node>();
        // init start node
        gScore.put(start, 0);
        start.setfScore(heuristic.getValue(start.getState()));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // gets and removes first item
            var current = openSet.poll();
            // set solution and break if goal
            if (current.getState().isGoal()) {
                reconstructPath(cameFrom, current);
                return;
            }
            // add current node to closed list
            closedSet.add(current);
            // for each reachable node
            for(var node: current.expand()) {
                // set score start values to "infinity"
                if (!gScore.containsKey(node)) {
                    gScore.put(node, INFINITY);
                }
                // skip closed node
                if (!closedSet.contains(node)) {
                    // if not jet in open set add state
                    if (!openSet.contains(node)) {
                        openSet.add(node);
                    }
                    // if the value is worse than the old one skip update of values
                    if (node.getDepth() < gScore.get(node)) { // update values
                        cameFrom.put(node, current);
                        gScore.put(node, node.getDepth());
                        node.setfScore(gScore.get(node) + heuristic.getValue(node.getState()));
                    }
                }
            }
        }
        // no solution found
        this.path = null;
    }

}
