import java.util.ArrayList;
import java.util.List;

/**
 * Represents actions taken in one turn of draughts.
 */
public class Move {

    private int start;
    private List<Integer> offsets;

    public Move() {

    }

    public Move(int start, List<Integer> offsets) {

    }

    public Move(int start, int... offsets) {

    }

    public void makeMove(GameState state) {

    }

    public int getStart() {
        return 0;
    }

    public List<Integer> getOffsets() {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
