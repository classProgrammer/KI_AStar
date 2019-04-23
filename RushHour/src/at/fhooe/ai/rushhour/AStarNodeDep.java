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
public class AStarNodeDep {

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

    /**
     * This is the constructor that performs A* search to compute a
     * solution for the given puzzle using the given heuristic.
     */
    public AStarNodeDep(Puzzle puzzle, Heuristic heuristic) {

        var cameFrom = new HashMap<Node, Node>();
        var gScore = new HashMap<Node, Integer>();
        var fScore = new HashMap<Node, Integer>();
        var start = puzzle.getInitNode();
        var startState = start.getState();

        // comparator for order of elements in openSet
        Comparator<Node> comp = (a, b) -> {
            var aKey = a;
            var bKey = b;
//            var aKey = a.getState();
//            var bKey = b.getState();

            int aVal = fScore.get(aKey);
            int bVal = fScore.get(bKey);

            if (aVal == bVal) return 0;
            if (aVal > bVal) return 1;

            return -1;
        };

        // open and closed sets
        var openSet = new PriorityQueue<>(comp);
        var closedSet = new PriorityQueue<>(comp);

        // init start node
        gScore.put(start, 0);
        fScore.put(start, heuristic.getValue(startState));
        openSet.add(start);

        while (!openSet.isEmpty()) {
            var current = openSet.poll();
            var currentState = current.getState();

            // set solution and break if goal
            if (currentState.isGoal()) {
                System.out.println("Goal");
                reconstructPath(cameFrom, current);
                break;
            }

            closedSet.add(current);
            for(var neighbor: current.expand()) {
                var neighborState = neighbor.getState();

                // set node start values to "infinity"
                if (!gScore.containsKey(neighbor)) {
                    gScore.put(neighbor, Integer.MAX_VALUE);
                }
                if (!fScore.containsKey(neighbor)) {
                    fScore.put(neighbor, Integer.MAX_VALUE);
                }

                // ignore closed node
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // if not jet in open set add state
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }

                // costs one move ??? don't know what else to do
                var tentative_gScore = gScore.get(current) + 1;

                // if the value is worse than the old one don't update
                if (tentative_gScore >= gScore.get(neighbor)) {
                    continue;
                }

                // update values
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentative_gScore);
                fScore.put(neighbor, gScore.get(neighbor) + heuristic.getValue(neighborState));
            }
        }
        System.out.println("found");
        // no solution found
        this.path = null;
    }

}
