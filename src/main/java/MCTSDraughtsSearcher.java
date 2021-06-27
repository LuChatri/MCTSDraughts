import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Performs full iterations of Monte Carlo Tree Search with UCT for draughts.
 */
public class MCTSDraughtsSearcher {

    public MCTSNode root;
    private double explorationParameter;

    /**
     * Constructs a new MCTSDraughtsSearcher.
     *
     * @param gameState Game state to search from.
     * @param explorationParameter Constant parameter used in MCTS to balance exploration and explitation.
     */
    public MCTSDraughtsSearcher(GameState gameState, double explorationParameter) {
        root = new MCTSNode(0, 0, null, new ArrayList<>(), gameState);
        setExplorationParameter(explorationParameter);
    }

    /**
     * Returns the best move (or moves if a tie occurs) according to current search results.
     *
     * @return the best move(s).
     */
    public List<Move> getBestMoves() {
        // Find the most visited child game states.
        List<GameState> bestStates = new ArrayList<>();
        int maxVisits = Integer.MIN_VALUE;
        for (MCTSNode child : root.getChildren()) {
            if (child.getVisits() > maxVisits) {
                bestStates.clear();
                maxVisits = child.getVisits();
            }

            if (child.getVisits() >= maxVisits) {
                bestStates.add(child.getData());
            }
        }

        // MCTSNodes don't store move data, so reverse-engineer the moves.
        // For all possible moves, check if the state after making the move
        // is in the list of most visited game states.
        List<Move> bestMoves = new ArrayList<>();
        for (Move move : root.getData().generateLegalMoves()) {
            GameState gameState = new GameState(root.getData());
            gameState.makeMove(move);
            gameState.swapActivePlayer();
            if (bestStates.contains(gameState)) {
                bestMoves.add(move);
            }
        }

        return bestMoves;
    }

    /**
     * Performs one iteration of Monte Carlo Tree Search with UCT.
     */
    public void searchOnce() {
        MCTSNode toExpand = select(root);
        MCTSNode toSimulate = expand(toExpand);
        String winner = simulate(toSimulate);

        if (winner.equals(toSimulate.getData().getActivePlayer())) {
            backpropagate(0.0, toSimulate);
        } else {
            backpropagate(1.0, toSimulate);
        }
    }

    /**
     * Selects a leaf node of the given mode using UCT.
     *
     * @param node Node to search from.
     * @return Best node to expand and simulate per UCT.
     */
    public MCTSNode select(MCTSNode node) {
        while (!(node.isLeaf())) {
            // Find and select the child with the highest UCT value.
            MCTSNode highNode = null;
            double highUCTValue = Double.NEGATIVE_INFINITY;
            for (MCTSNode child: node.getChildren()) {
                double UCTValue = child.getUCTValue(getExplorationParameter());
                if (UCTValue > highUCTValue) {
                    highNode = child;
                    highUCTValue = UCTValue;
                    if (highUCTValue == Double.POSITIVE_INFINITY) { break; }
                }
            }
            node = highNode;
        }
        return node;
    }

    /**
     * Adds child nodes corresponding to legal moves of a given node.
     *
     * If this node is terminal, it will be returned instead of its child.
     *
     * @param node Node to create children for.
     * @return A randomly chosen child node for which to perform a playout.
     */
    public MCTSNode expand(MCTSNode node) {
        // Create child nodes.
        GameState gs = node.getData();
        for (Move move : gs.generateLegalMoves()) {
            GameState newNodeGameState = new GameState(gs);
            newNodeGameState.makeMove(move);
            newNodeGameState.swapActivePlayer();
            MCTSNode newNode = new MCTSNode(0, 0, node, new ArrayList<>(), newNodeGameState);
            node.addChild(newNode);
        }

        List<MCTSNode> children = node.getChildren();
        // If this node is terminal, return it instead of a child/
        if (children.size() == 0) { return node; }
        // Randomly select a child to simulate.
        final Random rand = new Random();
        return children.get(rand.nextInt(children.size()));
    }

    /**
     * Performs a random playout from a given node.
     *
     * @param node Node to simulate from.
     * @return The winner of the simulation, represented by "W" for wite or "B" for black.
     */
    public String simulate(MCTSNode node) {
        final Random rand = new Random();
        final int maxMoves = 1000;
        int count = 0;
        GameState gs = new GameState(node.getData());
        while (count < maxMoves) {
            count++;
            List<Move> legalMoves = gs.generateLegalMoves();
            if (legalMoves.size() == 0) { break; }
            Move toMake = legalMoves.get(rand.nextInt(legalMoves.size()));
            gs.makeMove(toMake);
            gs.swapActivePlayer();
        }
        gs.swapActivePlayer(); // Return the winner, not the loser.
        return gs.getActivePlayer();
    }

    /**
     * Updates a node and its parents with a game outcome.
     *
     * @param outcome 1 for a win, 0 for a loss, 0.5 for a draw
     * @param node Node that experienced the given outcome.
     */
    public void backpropagate(double outcome, MCTSNode node) {
        node.setVisits(node.getVisits()+1);
        node.setValue(node.getValue()+outcome);
        MCTSNode parent = node.getParent();
        if (parent != null) {
            // Since the active player in the parent node is the inactive
            // player in this node, a win in this node is a loss for the
            // parent and vice versa. Hence why we backpropagate w/ 1.0-outcome.
            backpropagate(1.0 - outcome, node.getParent());
        }
    }

    public double getExplorationParameter() {
        return explorationParameter;
    }

    public void setExplorationParameter(double explorationParameter) {
        this.explorationParameter = explorationParameter;
    }

}
