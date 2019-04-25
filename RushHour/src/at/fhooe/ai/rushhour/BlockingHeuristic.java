package at.fhooe.ai.rushhour;

public class BlockingHeuristic implements Heuristic {

    public BlockingHeuristic(Puzzle puzzle) {/*it's empty*/}

    public int getValue(State state) {
        // solved = no further move necessary
        if (state.isGoal()) return 0;

        int[][] grid = state.getGrid();
        Puzzle puzzle = state.getPuzzle();
        int gridSize = puzzle.getGridSize();
        // per definition 1 + number of blocking cars
        int noOfCarsBlocking = 1;
        // target car has index 0
        var isVertical = puzzle.getCarOrient(0) == true;

        // x and y coordinates of target car
        int fixed = puzzle.getFixedPosition(0);
        // skip the target cars body in the direction it moves
        int variable = state.getVariablePosition(0) + puzzle.getCarSize(0);

        // look right(horizontal) or below(vertical) for obstacles
        for (int i = variable; i < gridSize; ++i) {
            int v = isVertical ? grid[fixed][i] : grid[i][fixed];
            // values of zero or greater represent an obstacle
            if (v > -1) ++noOfCarsBlocking;
        }
        return noOfCarsBlocking;
    }
}
