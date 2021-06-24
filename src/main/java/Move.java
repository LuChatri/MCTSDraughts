import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents actions taken in one turn of draughts.
 */
public class Move {

    private final int start;
    private final List<Offset> offsets;

    /**
     * Constructs a move.
     *
     * A Move has two components: a start and a series of offsets. The start
     * is the square upon which the moving piece began its turn. Squares are
     * numbered from top left to bottom right <b>starting at 1 and excluding
     * the 32 squares not used in draughts.</b>. Offsets are actions (jumps
     * or normal moves) taken sequentially from the start square.
     *
     * @param start The square (1-32) that the moving piece started on.
     * @param offsets Actions taken by the moving piece.
     */
    public Move(int start, List<Offset> offsets) {
        if (start > 32 || start < 1) {
            throw new IllegalArgumentException("start should be in the range 1-32 inclusive");
        }
        this.start = start;
        this.offsets = new ArrayList<>(offsets);
        for (int i = this.offsets.size()-1; i >= 0; i--) {
            if (this.offsets.get(i) == null) {
                this.offsets.remove(i);
            }
        }
    }

    /**
     * @see #Move(int, List)
     */
    public Move(int start, Offset... offsets) {
        this(start, Arrays.asList(offsets));
    }

    public int getStart() {
        return start;
    }

    public List<Offset> getOffsets() {
        return offsets;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Move: ");
        builder.append(start);
        for (Offset o : getOffsets()) {
            builder.append("-->");
            builder.append(o.toString());
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return start == move.start && Objects.equals(offsets, move.offsets);
    }
}
