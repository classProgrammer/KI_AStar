package at.fhooe.ai.rushhour;

import java.util.*;

public class State {

    private Puzzle puzzle;
    private int[] varPos;
    private int hashcode;

    public State(Puzzle puzzle, int[] varPos) {
        this.puzzle = puzzle;
        this.varPos = varPos;
        computeHashCode();
    }

    public boolean isGoal() {
        return (varPos[0] == puzzle.getGridSize() - 1);
    }

    public int getVariablePosition(int v) {
        return varPos[v];
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void print() {
        int[][] grid = getGrid();
        int gridSize = puzzle.getGridSize();

        System.out.print("+-");
        for (int x = 0; x < gridSize; x++) {
            System.out.print("--");
        }
        System.out.println("+");

        for (int y = 0; y < gridSize; y++) {
            System.out.print("| ");
            for (int x = 0; x < gridSize; x++) {
                int v = grid[x][y];
                if (v < 0) {
                    System.out.print(". ");
                } else {
                    int size = puzzle.getCarSize(v);
                    if (puzzle.getCarOrient(v)) {
                        System.out.print((y == varPos[v] ? "^ " : ((y == varPos[v] + size - 1) ? "v " : "| ")));
                    } else {
                        System.out.print(x == varPos[v] ? "< " : ((x == varPos[v] + size - 1) ? "> " : "- "));
                    }
                }
            }
            System.out.println((puzzle.getCarOrient(0) || y != puzzle.getFixedPosition(0)) ? "|" : (isGoal() ? ">" : " "));
        }

        System.out.print("+-");
        for (int x = 0; x < gridSize; x++) {
            System.out.print((!puzzle.getCarOrient(0) || x != puzzle.getFixedPosition(0)) ? "--" : (isGoal() ? "v-" : " -"));
        }
        System.out.println("+");

    }

    public int[][] getGrid() {
        int gridSize = puzzle.getGridSize();
        int[][] grid = new int[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++)
            for (int j = 0; j < gridSize; j++)
                grid[i][j] = -1;

        int num_cars = puzzle.getNumCars();

        for (int v = 0; v < num_cars; v++) {
            boolean orient = puzzle.getCarOrient(v);
            int size = puzzle.getCarSize(v);
            int fp = puzzle.getFixedPosition(v);
            if (v == 0 && varPos[v] + size > gridSize)
                size--;
            if (orient) {
                for (int d = 0; d < size; d++)
                    grid[fp][varPos[v] + d] = v;
            } else {
                for (int d = 0; d < size; d++)
                    grid[varPos[v] + d][fp] = v;
            }
        }
        return grid;

    }

    public State[] expand() {
        int gridSize = puzzle.getGridSize();
        int[][] grid = getGrid();
        int num_cars = puzzle.getNumCars();

        ArrayList<State> new_states = new ArrayList<State>();

        for (int v = 0; v < num_cars; v++) {
            int p = varPos[v];
            int fp = puzzle.getFixedPosition(v);
            boolean orient = puzzle.getCarOrient(v);
            for (int np = p - 1; np >= 0 && (orient ? grid[fp][np] : grid[np][fp]) < 0; np--) {
                int[] newVarPos = varPos.clone();
                newVarPos[v] = np;
                new_states.add(new State(puzzle, newVarPos));
            }

            int carsize = puzzle.getCarSize(v);
            for (int np = p + carsize; (np < gridSize && (orient ? grid[fp][np] : grid[np][fp]) < 0)
                    || (v == 0 && np == gridSize); np++) {
                int[] newVarPos = varPos.clone();
                newVarPos[v] = np - carsize + 1;
                new_states.add(new State(puzzle, newVarPos));
            }
        }

        puzzle.incrementSearchCount(new_states.size());

        return new_states.toArray(new State[0]);
    }

    private void computeHashCode() {
        hashcode = puzzle.hashCode();
        for (int i = 0; i < varPos.length; i++)
            hashcode = 31 * hashcode + varPos[i];
    }

    public int hashCode() {
        return hashcode;
    }


    public boolean equals(Object o) {
        State s;
        try {
            s = (State) o;
        } catch (ClassCastException e) {
            return false;
        }
        if (hashcode != s.hashcode || !puzzle.equals(s.puzzle))
            return false;

        for (int i = 0; i < varPos.length; i++)
            if (varPos[i] != s.varPos[i])
                return false;
        return true;
    }
}
