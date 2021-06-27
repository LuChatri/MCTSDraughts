import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MCTSDraughtsSearcher {

    public MCTSNode root;
    private double explorationParameter;

    public MCTSDraughtsSearcher(GameState gameState, double explorationParameter) {
        root = new MCTSNode(0, 0, null, new ArrayList<>(), gameState);
        setExplorationParameter(explorationParameter);
    }

    public List<Move> getBestMoves() {
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

    public MCTSNode expand(MCTSNode node) {
        GameState gs = node.getData();
        for (Move move : gs.generateLegalMoves()) {
            GameState newNodeGameState = new GameState(gs);
            newNodeGameState.makeMove(move);
            newNodeGameState.swapActivePlayer();
            MCTSNode newNode = new MCTSNode(0, 0, node, new ArrayList<>(), newNodeGameState);
            node.addChild(newNode);
        }

        final Random rand = new Random();
        List<MCTSNode> children = node.getChildren();
        if (children.size() == 0) { return node; }
        return children.get(rand.nextInt(children.size()));
    }

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

    public void backpropagate(double outcome, MCTSNode node) {
        node.setVisits(node.getVisits()+1);
        node.setValue(node.getValue()+outcome);
        MCTSNode parent = node.getParent();
        if (parent != null) {
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
