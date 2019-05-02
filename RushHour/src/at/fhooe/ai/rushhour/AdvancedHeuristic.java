package at.fhooe.ai.rushhour;

import java.util.HashSet;
import java.util.Set;

public class AdvancedHeuristic implements Heuristic {

    private final int targetCarIndex = 0;

    private Puzzle puzzle;
    private int[][] grid;
    private int gridSize;

    private int cost = 1;
    private Set<Integer> blockingCarsSetAbove = new HashSet<>();
    private Set<Integer> blockingCarsSetBelow = new HashSet<>();

    private boolean isVertical;
    private int fixed;
    private int variable;

    public AdvancedHeuristic(Puzzle puzzle) {
    }

    /* ABSTRACT:
     * Idea is similar to BlockingHeuristic:
     * IF: target is at goal position return 0
     * Else:
     * - Count the number of cars between target car and goal (first order blocking cars)
     * - Further, count the number of cars blocking above and below the target path for the
     *      size of corresponding first order blocking car so that it could move out of the way
     *      (second order blocking cars)
     * - Use the smaller second order blocking car (above/below target path)
     * - Add the first order blocking cars and the  second order blocking cars together for
     *      the final cost.
     * - IF: none are blocking, return still a cost of 1 because we are not at the goal position
     **/
    public int getValue(State state) {
        if (state.isGoal()) return 0;

        initStateValues(state);

        // starts already in front of target car
        for (var i = variable + puzzle.getCarSize(targetCarIndex); i < gridSize; i++) {

            int firstOrderBlockingIndex = gridAt(i, fixed);
            if (indexIsNotEmptyAndNotExcludedIndex(firstOrderBlockingIndex, targetCarIndex)) {

                var blockingCarSize = puzzle.getCarSize(firstOrderBlockingIndex);

                var blockingAbove = getBlockingCount(
                        firstOrderBlockingIndex, blockingCarSize, -blockingCarSize, blockingCarsSetAbove);
                var blockingBelow = getBlockingCount(
                        firstOrderBlockingIndex, blockingCarSize, 1, blockingCarsSetBelow);

                // first order + second order blocking cars
                cost += 1 + getNumberOfBlockingCars(blockingAbove, blockingBelow, firstOrderBlockingIndex);
            }
        }
        return cost;
    }

    private void initStateValues(State state) {
        grid = state.getGrid();
        puzzle = state.getPuzzle();
        gridSize = puzzle.getGridSize();

        // if puzzle is not solved, cost should be at least 1
        cost = 1;
        blockingCarsSetAbove = new HashSet<>();
        blockingCarsSetBelow = new HashSet<>();

        isVertical = puzzle.getCarOrient(targetCarIndex);
        fixed = puzzle.getFixedPosition(targetCarIndex);
        variable = state.getVariablePosition(targetCarIndex);// + puzzle.getCarSize(targetCarIndex);
    }

    private int gridAt(int x, int y) {
        return isVertical ? grid[y][x] : grid[x][y];
    }

    private int getBlockingCount(
            int firstOrderBlockingIndex, int blockingCarSize, int offset, Set<Integer> set) {

        int blockingCount = 0;
        int start = fixed + offset;

        for (var gapIterator = start; gapIterator < start + blockingCarSize; gapIterator++) {
            // car does not fit between edge of grid and target path
            if (gapIterator < 0 || gapIterator >= gridSize) {
                return -1;
            }

            var secondOrderBlockingIndex = gridAt(puzzle.getFixedPosition(firstOrderBlockingIndex), gapIterator);

            if (indexIsNotEmptyAndNotExcludedIndex(secondOrderBlockingIndex, firstOrderBlockingIndex) &&
                    !set.contains(secondOrderBlockingIndex)) {

                set.add(secondOrderBlockingIndex);
                blockingCount++;
            }
        }
        return blockingCount;
    }

    private boolean indexIsNotEmptyAndNotExcludedIndex(int blockingIndex, int excludedIndex) {
        return blockingIndex != -1 && blockingIndex != excludedIndex;
    }

    private int getNumberOfBlockingCars(int blockingCountAbove, int blockingCountBelow, int firstOrderBlockingIndex) {
        var numberOfBlockingCars = 0;
        if (blockingCountAbove == -1 && blockingCountBelow == -1) {
            throw new Error("Car with Index " + firstOrderBlockingIndex + " blocks the goal for target car.");
        } else if (blockingCountAbove == -1) {
            numberOfBlockingCars = blockingCountBelow;
            blockingCarsSetAbove.clear();
        } else if (blockingCountBelow == -1) {
            numberOfBlockingCars = blockingCountAbove;
            blockingCarsSetBelow.clear();
        } else {
            numberOfBlockingCars = Math.min(blockingCountAbove, blockingCountBelow);
        }
        return numberOfBlockingCars;
    }
}
