import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class GameStateTests {

    @ParameterizedTest
    @ValueSource(strings= {
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
    }

}
