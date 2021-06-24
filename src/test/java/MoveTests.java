import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class MoveTests {

    @Test
    public void testMove() {
        Move m;

        m = new Move(1);
        Assertions.assertEquals(1, m.getStart());
        Assertions.assertEquals(new ArrayList<>(), m.getOffsets());

        m = new Move(31, Offset.MOVE_SOUTHEAST, Offset.MOVE_SOUTHWEST, Offset.MOVE_NORTHWEST, null);
        Assertions.assertEquals(31, m.getStart());
        Assertions.assertEquals(Arrays.asList(Offset.MOVE_SOUTHEAST, Offset.MOVE_SOUTHWEST, Offset.MOVE_NORTHWEST),
                m.getOffsets());
        Assertions.assertEquals("Move: 31-->MOVE_SOUTHEAST-->MOVE_SOUTHWEST-->MOVE_NORTHWEST",
                m.toString());

        m = new Move(5, (Offset) null);
        Assertions.assertEquals(new ArrayList<>(), m.getOffsets());
        Assertions.assertEquals("Move: 5", m.toString());

        new Move(32);
        new Move(1, null, Offset.JUMP_NORTHEAST, Offset.JUMP_NORTHWEST, Offset.JUMP_SOUTHWEST);
        new Move(16, Arrays.asList(Offset.MOVE_NORTHEAST, null, Offset.JUMP_SOUTHEAST));

        Assertions.assertThrows(IllegalArgumentException.class, ()->new Move(0));
        Assertions.assertThrows(IllegalArgumentException.class, ()->new Move(33));
    }

}
