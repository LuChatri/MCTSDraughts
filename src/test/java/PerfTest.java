import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class PerfTest {

    public static int perft(GameState gameState, int depth) {
        if (depth <= 0) {
            throw new IllegalArgumentException("depth must be >=1");
        } else if (depth == 1) {
            return gameState.generateLegalMoves().size();
        } else {
            int nodes = 0;
            List<Move> moves = gameState.generateLegalMoves();
            for (Move move : moves) {
                GameState gameStateCopy = new GameState(gameState);
                gameStateCopy.makeMove(move);
                gameStateCopy.swapActivePlayer();
                nodes += perft(gameStateCopy, depth-1);
            }
            return nodes;
        }
    }

    @ParameterizedTest
    @MethodSource("perftParameterGenerator")
    public void perftTest(int depth, int nodesExpected) {
        GameState gs = new GameState();
        Assertions.assertEquals(nodesExpected, perft(gs, depth));
    }

    public static Stream<Arguments> perftParameterGenerator() {
        return Stream.of(
                Arguments.of(1, 7),
                Arguments.of(2, 49),
                Arguments.of(3, 302),
                Arguments.of(4, 1469),
                Arguments.of(5, 7361),
                Arguments.of(6, 36768),
                Arguments.of(7, 179740),
                Arguments.of(8, 845931),
                Arguments.of(9, 3963680),
                Arguments.of(10, 18391564)
        );
    }

}
