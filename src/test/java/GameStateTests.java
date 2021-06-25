import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

public class GameStateTests {

    @ParameterizedTest
    @ValueSource(strings={
            "B:WK10,K15,18,24,27,28:B12,16,20,K22,K25,K29",
            "B:W18,19,21,23,24,26,29,30,31,32:B1,2,3,4,6,7,9,10,11,12",
            "W:W5,6,7,11,18,19,27:B2,3,4,9,10,17,20,25,26,28",
            "W:W21,22,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,9,10,11,12",
            "B:WK18,19,20:BK32",
            "W:WK18,19,20:BK32",
            "W:W:B",
            "B:W:B"
    })
    public void testGameState(String FEN) {
        Assertions.assertEquals(FEN, (new GameState(FEN)).toString());
    }

    @Test
    public void testGameState() {
        Assertions.assertEquals("B:W21,22,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,9,10,11,12",
                (new GameState()).toString());
        Assertions.assertEquals("B:W:B", (new GameState(new GameState("B:W:B"))).toString());
        Assertions.assertThrows(IllegalArgumentException.class, ()->new GameState("C:W:B"));
        Assertions.assertThrows(IllegalArgumentException.class, ()->new GameState("B:W33:B"));
        Assertions.assertThrows(IllegalArgumentException.class, ()->new GameState("B:W0:B"));
        Assertions.assertThrows(IllegalArgumentException.class, ()->new GameState("B:W-:B"));
        Assertions.assertThrows(IllegalArgumentException.class, ()->new GameState("B:WK-:B"));
    }

    @Test
    public void testAddOffsetToSquare() {

    }

    @Test
    public void testMakeMove() {
        List<Move> moves = new ArrayList<>(Arrays.asList(
                new Move(19, Offset.MOVE_NORTHWEST),
                new Move(10, Offset.JUMP_SOUTHEAST),
                new Move(5, Offset.MOVE_NORTHEAST),
                new Move(3, Offset.JUMP_SOUTHWEST),
                new Move(11, Offset.MOVE_NORTHEAST),
                new Move(4, Offset.JUMP_SOUTHWEST),
                new Move(27, Offset.MOVE_NORTHEAST),
                new Move(20, Offset.JUMP_SOUTHWEST),
                new Move(18, Offset.MOVE_NORTHWEST),
                new Move(9, Offset.JUMP_SOUTHEAST),
                new Move(1, Offset.MOVE_SOUTHWEST),
                new Move(2, Offset.JUMP_SOUTHWEST),
                // The Big One
                new Move(5, Offset.JUMP_SOUTHEAST, Offset.JUMP_NORTHEAST, Offset.JUMP_SOUTHEAST,
                        Offset.JUMP_SOUTHWEST, Offset.JUMP_NORTHWEST, Offset.JUMP_SOUTHWEST,
                        Offset.JUMP_SOUTHEAST, Offset.JUMP_NORTHEAST, Offset.JUMP_SOUTHEAST)
        ));

        List<String> expectedFENs = new ArrayList<>(Arrays.asList(
                "B:W5,6,7,11,15,18,27:B2,3,4,9,10,17,20,25,26,28",
                "W:W5,6,7,11,18,27:B2,3,4,9,17,19,20,25,26,28",
                "B:WK1,6,7,11,18,27:B2,3,4,9,17,19,20,25,26,28",
                "W:WK1,6,11,18,27:B2,4,9,10,17,19,20,25,26,28",
                "B:WK1,6,8,18,27:B2,4,9,10,17,19,20,25,26,28",
                "W:WK1,6,18,27:B2,9,10,11,17,19,20,25,26,28",
                "B:WK1,6,18,24:B2,9,10,11,17,19,20,25,26,28",
                "W:WK1,6,18:B2,9,10,11,17,19,25,26,27,28",
                "B:WK1,6,14:B2,9,10,11,17,19,25,26,27,28",
                "W:WK1,6:B2,10,11,17,18,19,25,26,27,28",
                "B:WK5,6:B2,10,11,17,18,19,25,26,27,28",
                "W:WK5:B9,10,11,17,18,19,25,26,27,28",
                "B:WK32:B28"
        ));

        // These two sets of test data should correspond 1:1.
        Assertions.assertEquals(moves.size(), expectedFENs.size());

        GameState gs = new GameState("W:W27,19,18,11,7,6,5:B28,26,25,20,17,10,9,4,3,2");
        for (int i = 0; i < moves.size(); i++) {
            gs.makeMove(moves.get(i));
            gs.swapActivePlayer();
            String expectedFEN = expectedFENs.get(i);
            Assertions.assertEquals((new GameState(expectedFEN)).toString(), gs.toString());
        }
    }

}
