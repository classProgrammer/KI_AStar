package at.fhooe.ai.rushhour;

import java.util.HashSet;
import java.util.Set;

public class AdvancedHeuristic implements Heuristic {

    private final int targetCarIndex = 0;

    public AdvancedHeuristic(Puzzle puzzle) {}

    /* ABSTRACT:
    * Idea is similar to BlockingHeuristic:
    * If target is at goal position return 0
    * Else:
    * - Count the number of cars between target car and goal
    * - Moreover, count cars perpendicular blocking first order blocking cars (only once)
    * - Use absolute number of blocking cars + 1 as cost
    *   - if none are blocking, still a cost of 1 because we are not at the goal position
    **/
    public int getValue(State state) {

        if (state.isGoal()) return 0;

        var grid = state.getGrid();
        var puzzle = state.getPuzzle();
        var gridSize = puzzle.getGridSize();

        // if puzzle is not solved, cost should be at least 1
        var cost = 1;
        var secondOrderBlocking = 0;
        Set<Integer> blockingCars = new HashSet<>();

        var isVertical = puzzle.getCarOrient(targetCarIndex);
        var fixed = puzzle.getFixedPosition(targetCarIndex);
        var variable = state.getVariablePosition(targetCarIndex) + puzzle.getCarSize(targetCarIndex);

        // starts already in front of target car
        for (var i = variable; i < gridSize; i++) {
            int firstOrderBlockingIndex = isVertical ?
                    grid[fixed][i] :
                    grid[i][fixed];

            if (indexIsnotEmptyAndNotTarget(firstOrderBlockingIndex)) {

                secondOrderBlocking = 0;
                for (var crossingIterator = 0; crossingIterator < gridSize; crossingIterator++) {

                    var secondOrderBlockingIndex = isVertical ?
                            grid[crossingIterator][i] :
                            grid[i][crossingIterator];

                    if (indexIsnotEmptyAndNotTarget(secondOrderBlockingIndex) &&
                            secondOrderBlockingIndex != firstOrderBlockingIndex
                            && !blockingCars.contains(secondOrderBlockingIndex)) {

                        blockingCars.add(secondOrderBlockingIndex);

                        // second order blockings -> perpendicular to targent car
                        secondOrderBlocking++;
                    }
                }
                cost += 1 + secondOrderBlocking;
            }
        }
        return cost;
    }

    private boolean indexIsnotEmptyAndNotTarget(int firstOrderBlockingIndex) {
        return firstOrderBlockingIndex != -1 &&
            firstOrderBlockingIndex != targetCarIndex;
    }
}
