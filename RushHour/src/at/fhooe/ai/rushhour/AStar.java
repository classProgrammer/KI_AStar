package at.fhooe.ai.rushhour;

import java.util.*;

/**
 * This is the template for a class that performs A* search on a given rush hour
 * puzzle with a given heuristic. The main search computation is carried out by
 * the constructor for this class, which must be filled in. The solution (a path
 * from the initial state to a goal state) is returned as an array of
 * <tt>State</tt>s called <tt>path</tt> (where the first element
 * <tt>path[0]</tt> is the initial state). If no solution is found, the
 * <tt>path</tt> field should be set to <tt>null</tt>. You may also wish to
 * return other information by adding additional fields to the class.
 */
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
    /**
     * This is the constructor that performs A* search to compute a
     * solution for the given puzzle using the given heuristic.
     */
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
