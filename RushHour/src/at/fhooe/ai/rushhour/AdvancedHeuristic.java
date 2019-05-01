package at.fhooe.ai.rushhour;

import java.util.HashSet;
import java.util.Set;

public class AdvancedHeuristic implements Heuristic {

    private final int targetCarIndex = 0;

    public AdvancedHeuristic(Puzzle puzzle) {
    }

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
        Set<Integer> blockingCarsSetAbove = new HashSet<>();
        Set<Integer> blockingCarsSetBelow = new HashSet<>();

        var isVertical = puzzle.getCarOrient(targetCarIndex);
        var fixed = puzzle.getFixedPosition(targetCarIndex);
        var variable = state.getVariablePosition(targetCarIndex) + puzzle.getCarSize(targetCarIndex);

        // starts already in front of target car
        for (var i = variable; i < gridSize; i++) {
            int firstOrderBlockingIndex = isVertical ?
                    grid[fixed][i] :
                    grid[i][fixed];

            if (indexIsNotEmptyAndNotTarget(firstOrderBlockingIndex)) {

                var blockingCarSize = puzzle.getCarSize(firstOrderBlockingIndex);

                var blockingCountAbove = -1;
                var blockingCountBelow = -1;

                if (canBePlacedAboveTargetPath(blockingCarSize, fixed)) {
                    blockingCountAbove = 0;

                    for (var gapIterator = 0; gapIterator < blockingCarSize; gapIterator++) {
                        var secondOrderBlockingIndex = grid[puzzle.getFixedPosition(firstOrderBlockingIndex)][fixed - gapIterator - 1];
                        if (indexIsNotEmptyAndNotTargetOrExcludedIndex(secondOrderBlockingIndex, firstOrderBlockingIndex) &&
                                !blockingCarsSetAbove.contains(secondOrderBlockingIndex)) {

                            blockingCarsSetAbove.add(secondOrderBlockingIndex);
                            blockingCountAbove++;
                        }
                    }
                }

                if (canBePlacedBelowTargetPath(blockingCarSize, fixed, puzzle.getGridSize())) {
                    blockingCountBelow = 0;

                    for (var gapIterator = 0; gapIterator < blockingCarSize; gapIterator++) {
                        var secondOrderBlockingIndex = grid[puzzle.getFixedPosition(firstOrderBlockingIndex)][fixed + gapIterator + 1];

                        if (indexIsNotEmptyAndNotTargetOrExcludedIndex(secondOrderBlockingIndex, firstOrderBlockingIndex) &&
                                !blockingCarsSetBelow.contains(secondOrderBlockingIndex)) {
                            blockingCarsSetBelow.add(secondOrderBlockingIndex);
                            blockingCountBelow++;
                        }
                    }
                }

                var numberOfBlockingCars = 0;

                if (blockingCountAbove == -1 && blockingCountBelow == -1) {
                    throw new Error("Car with Index " + firstOrderBlockingIndex + "blocks the goal for target car.");
                } else if (blockingCountAbove == -1) {
                    numberOfBlockingCars = blockingCountBelow;
                    blockingCarsSetAbove.clear();
                } else if (blockingCountBelow == -1) {
                    numberOfBlockingCars = blockingCountAbove;
                    blockingCarsSetBelow.clear();
                } else {
                    numberOfBlockingCars = Math.min(blockingCountAbove, blockingCountBelow);
                }

                cost += 1 + numberOfBlockingCars;
            }
        }
        return cost;
    }

    private boolean canBePlacedAboveTargetPath(int blockingCarSize, int targetFixedPosition) {
        return blockingCarSize <= targetFixedPosition;
    }

    private boolean canBePlacedBelowTargetPath(int blockingCarSize, int targetFixedPosition, int gridSize) {
        return gridSize - blockingCarSize > targetFixedPosition;
    }

    private boolean indexIsNotEmptyAndNotTarget(int blockingIndex) {
        return blockingIndex != -1 &&
                blockingIndex != targetCarIndex;
    }

    private boolean indexIsNotEmptyAndNotTargetOrExcludedIndex(int blockingIndex, int excludedIndex) {
        return blockingIndex != -1 &&
                blockingIndex != targetCarIndex &&
                blockingIndex != excludedIndex;
    }
}
