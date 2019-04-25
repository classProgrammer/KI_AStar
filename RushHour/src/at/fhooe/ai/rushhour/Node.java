package at.fhooe.ai.rushhour;

/**
 * This is the class for representing a single search node. Such a node consists
 * of a <tt>State</tt> representing the actual configuration of the puzzle, as
 * well as the depth of the search node from the initial state, and the parent
 * node of the current node in the search tree, i.e., the node from which this
 * node was expanded. Thus, the path from the initial node to this node can be
 * computed by tracing backward using the parent links. (The parent of the
 * initial node is set to <tt>null</tt>.) The total distance from the initial
 * node is equal to the depth of this node.
 */
public class Node implements Comparable<Node> {

    private State state;
    private int depth;
    private Node parent;
    private int fScore = Integer.MAX_VALUE;

    public Node(State state, int depth, Node parent) {
        this.state = state;
        this.depth = depth;
        this.parent = parent;
    }

    public int getfScore() {
        return fScore;
    }

    public void setfScore(int fScore) {
        this.fScore = fScore;
    }

    @Override
    public int compareTo(Node o) {
        Integer aVal = this.getfScore();
        Integer bVal = o.getfScore();

        return aVal.compareTo(bVal);
    }

    public State getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }


    public int getDepth() {
        return depth;
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }

    public boolean equals(Object o) {
        return this.state.equals(((Node) o).state);
    }

    public Node[] expand() {
        State[] new_states = state.expand();
        Node[] new_nodes = new Node[new_states.length];

        for (int i = 0; i < new_states.length; i++)
            new_nodes[i] = new Node(new_states[i], depth + 1, this);

        return new_nodes;
    }

}
