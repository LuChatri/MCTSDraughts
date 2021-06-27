import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

public class MCTSNodeTests {

    @Test
    public void testMCTSNode() {
        try {
            new MCTSNode();
            new MCTSNode(0, 0, null, new ArrayList<>(), null);
            new MCTSNode(new MCTSNode());

            ArrayList<MCTSNode> children = new ArrayList<>();
            children.add(new MCTSNode());
            new MCTSNode(0, 0, new MCTSNode(), children, new GameState());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTreeBuilding() {
        MCTSNode root = new MCTSNode();
        MCTSNode child = new MCTSNode(root);
        MCTSNode grandchild1 = new MCTSNode(child);
        MCTSNode grandchild2 = new MCTSNode(child);

        child.addChild(grandchild1).addChild(grandchild2);
        root.addChild(child);

        Assertions.assertEquals(1, root.getChildren().size());
        Assertions.assertEquals(2, child.getChildren().size());
        child.removeChild(grandchild1);
        Assertions.assertEquals(1, child.getChildren().size());
        child.setChildren(new ArrayList<>(Arrays.asList(grandchild1, grandchild2)));
        Assertions.assertEquals(2, child.getChildren().size());

        Assertions.assertTrue(root.isRoot());
        Assertions.assertTrue(grandchild1.isLeaf());
        Assertions.assertFalse(root.isLeaf());
        Assertions.assertFalse(child.isRoot());
        Assertions.assertFalse(child.isLeaf());

        Assertions.assertEquals(child, grandchild1.getParent());
        Assertions.assertEquals(root, grandchild1.getParent().getParent());
        Assertions.assertEquals(root, grandchild2.getParent().getParent());

        GameState gs = new GameState();
        Assertions.assertEquals(gs, root.setData(gs).getData());

        Assertions.assertEquals(0, child.getVisits());
        Assertions.assertEquals(0.0, child.getValue());
        child.setVisits(1);
        child.setValue(1.0);
        Assertions.assertEquals(1, child.getVisits());
        Assertions.assertEquals(1.0, child.getValue());
    }

}
