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
            totalPath.add(current.getState());
        }
        path = new State[totalPath.size()];
        path = totalPath.toArray(path);
    }

    /**
     * This is the constructor that performs A* search to compute a
     * solution for the given puzzle using the given heuristic.
     */
    public AStar(Puzzle puzzle, Heuristic heuristic) {

        // save "cheapest paths"
        var cameFrom = new HashMap<Node, Node>();
        // cost from start node to the current node
        var gScore = new HashMap<Node, Integer>();
        // gScore + heuristic
        //var fScore = new HashMap<Node, Integer>();

        // start node
        var start = puzzle.getInitNode();
        var startState = start.getState();

        // open and closed sets
        var openSet = new PriorityQueue<Node>();
        var closedSet = new PriorityQueue<Node>();

        // init start node
        gScore.put(start, 0);
        start.setfScore(heuristic.getValue(startState));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            // gets and removes first item
            var current = openSet.poll();
            var currentState = current.getState();

            //currentState.print();

            // set solution and break if goal
            if (currentState.isGoal()) {
                System.out.println("Goal");
                reconstructPath(cameFrom, current);
                return;
            }

            // add current node to closed list
            closedSet.add(current);

            // for each reachable node
            for(var node: current.expand()) {
                var nodeState = node.getState();

                // set score start values to "infinity"
                if (!gScore.containsKey(node)) {
                    gScore.put(node, Integer.MAX_VALUE);
                }

                // skip closed node
                if (!closedSet.contains(node)) {
                    // if not jet in open set add state
                    if (!openSet.contains(node)) {
                        openSet.add(node);
                    }

                    // costs one move ??? don't know what else to do
                    var tentative_gScore = node.getDepth(); // gScore.get(current) + 1;

                    // if the value is worse than the old one skip update of values
                    if (tentative_gScore < gScore.get(node)) {
                        // update values
                        cameFrom.put(node, current);
                        gScore.put(node, tentative_gScore);
                        node.setfScore(gScore.get(node) + heuristic.getValue(nodeState));
                    }
                }
            }
        }
        System.out.println("no solution found");
        // no solution found
        this.path = null;
    }

}
