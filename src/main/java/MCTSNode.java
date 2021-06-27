import java.util.ArrayList;
import java.util.List;

/**
 * Represents a tree node containing information for Monte Carlo Tree Search.
 */
public class MCTSNode extends Node<MCTSNode, Move> {

    private double value;
    private int visits;

    /**
     * Constructs an MCTSNode.
     *
     * Note that children should be mutable (not from Arrays.asList or
     * the like) if you plan to call methods like {@link Node#addChild(Node)}.
     *
     * Although value and visits typically represent wins and playouts, you are
     * welcome to use them for something else. Thus, both accept negative values.
     *
     * @param value Number of wins in playouts from this node.
     * @param visits Number of playouts from this node.
     * @param parent Node one step higher in the tree.
     * @param children Node one step lower in the tree.
     * @param data The move associated with this node.
     */
    public MCTSNode(double value, int visits, MCTSNode parent, List<MCTSNode> children, Move data) {
        super(parent, children, data);
        setValue(value);
        setVisits(visits);
    }

    /**
     * Constructs a node with the specified parent and no children, data, value, or visits.
     */
    public MCTSNode(MCTSNode parent) {
        this(0, 0, parent, new ArrayList<>(), null);
    }

    /**
     * Constructs a root node with no children, data, value, or visits.
     */
    public MCTSNode() {
        this(0, 0, null, new ArrayList<>(), null);
    }

    /**
     * Gets the value of this node per Upper Confidence Bound Applied to Trees.
     *
     * If the node is unvisited or the root node, its UCT value is
     * {@link Double#POSITIVE_INFINITY}.
     *
     * @param explorationParameter UCT value used to balance exploration and exploitation.
     * @return UCT value of this node.
     */
    public double getUCTValue(double explorationParameter) {
        if (isRoot() || getVisits() == 0) {
            return Double.POSITIVE_INFINITY;
        }
        double exploitation = getValue() / getVisits();
        double exploration = explorationParameter * Math.sqrt(Math.log(getParent().getVisits()) / getVisits());
        return exploitation + exploration;
    }

    public double getValue() {
        return value;
    }

    public MCTSNode setValue(double value) {
        this.value = value;
        return getThis();
    }

    public int getVisits() {
        return visits;
    }

    public MCTSNode setVisits(int visits) {
        this.visits = visits;
        return getThis();
    }

    /**
     * Must be implemented per the abstract class Node.
     *
     * @return This node
     */
    public MCTSNode getThis() {
        return this;
    }

}
