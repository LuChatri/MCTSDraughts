import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GameState {

    private List<Piece> state;
    private String activePlayer;

    public GameState() {
        this("B:W21,22,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,9,10,11,12");
    }

    public GameState(GameState gameState) {
        this.state = gameState.state;
        this.activePlayer = gameState.activePlayer;
    }

    public GameState(String FEN) {
        state = new ArrayList<>(Collections.nCopies(32, Piece.NONE));

        String[] fen = FEN.toUpperCase().split(":");
        try {
            activePlayer = fen[0];
            assert(activePlayer.equals("W") || activePlayer.equals("B"));

            // Add white pieces.
            for (String pieceStr : fen[1].substring(1).split(",")) {
                if (pieceStr.equals("")) { continue; }
                boolean isKing = pieceStr.startsWith("K");
                // FENs are one-indexed and state is zero-indexed, so subtract 1.
                int square = Integer.parseInt(isKing ? pieceStr.substring(1) : pieceStr) - 1;
                state.set(square, isKing ? Piece.WHITE_KING : Piece.WHITE_MAN);
            }

            // Add black pieces.
            for (String pieceStr : fen[2].substring(1).split(",")) {
                if (pieceStr.equals("")) { continue; }
                boolean isKing = pieceStr.startsWith("K");
                // FENs are one-indexed and state is zero-indexed, so subtract 1.
                int square = Integer.parseInt(isKing ? pieceStr.substring(1) : pieceStr) - 1;
                state.set(square, isKing ? Piece.BLACK_KING : Piece.BLACK_MAN);
            }
        } catch (AssertionError e) {
            throw new IllegalArgumentException("FEN active player must be \"W\" or \"B\"");
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new IllegalArgumentException("FEN must be a valid checkers FEN");
        }
    }

    public String getActivePlayer() {
        return activePlayer;
    }

    public void swapActivePlayer() {
        activePlayer = activePlayer.equals("W") ? "B" : "W";
    }

    public List<Move> generateLegalMoves() {
        return new ArrayList<>();
    }

    public Move generateRandomLegalMove() {
        return new Move(1);
    }

    public void makeMove(Move move) {

    }

    @Override
    public String toString() {
        StringBuilder whitePieces = new StringBuilder(":W");
        StringBuilder blackPieces = new StringBuilder(":B");
        for (int i = 0; i < state.size(); i++) {
            // FENs are one-indexed and state is zero-indexed, so add 1.
            int fenValueOfSquare = i+1;
            switch (state.get(i)) {
                case NONE:
                    break;
                case WHITE_MAN:
                    whitePieces.append(fenValueOfSquare).append(",");
                    break;
                case WHITE_KING:
                    whitePieces.append("K").append(fenValueOfSquare).append(",");
                    break;
                case BLACK_MAN:
                    blackPieces.append(fenValueOfSquare).append(",");
                    break;
                case BLACK_KING:
                    blackPieces.append("K").append(fenValueOfSquare).append(",");
                    break;
            }
        }
        // Join everything up. Remove the extra comma after each color's pieces.
        return activePlayer
                + whitePieces.toString().replaceAll(",$", "")
                + blackPieces.toString().replaceAll(",$", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return Objects.equals(state, gameState.state) && Objects.equals(activePlayer, gameState.activePlayer);
    }
}
