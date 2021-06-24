import java.util.ArrayList;
import java.util.List;

public class GameState {

    private List<Piece> state;
    private String activePlayer;

    public GameState() {

    }

    public GameState(GameState gameState) {

    }

    public GameState(String FEN) {

    }

    public void getActivePlayer() {

    }

    public void swapActivePlayer() {

    }

    public List<Move> generateLegalMoves() {
        return new ArrayList<>();
    }

    public Move generateRandomLegalMove() {
        return new Move();
    }

    public void makeMove(Move move) {

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
