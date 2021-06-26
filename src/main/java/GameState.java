import java.util.*;

/**
 * Represents a draughts position.
 */
public class GameState {

    private final List<Piece> state;
    private String activePlayer;

    /**
     * Constructs a GameState representing draughts' starting position.
     */
    public GameState() {
        this("B:W21,22,23,24,25,26,27,28,29,30,31,32:B1,2,3,4,5,6,7,8,9,10,11,12");
    }

    /**
     * Copies a GameState.
     * @param gameState GameState to copy.
     */
    public GameState(GameState gameState) {
        this.state = new ArrayList<>(gameState.state);
        this.activePlayer = new String(gameState.activePlayer);
    }

    /**
     * Constructs a GameState from a string in Forsyth-Edwards Notation for draughts.
     *
     * For more on the structure of a draughts FEN,
     * {@see https://en.wikipedia.org/wiki/Portable_Draughts_Notation#Tag_Pairs}.
     * FENs accepted by this constructor cannot have trailing periods.
     *
     * This constructor will not always error out on invalid FENs.
     *
     * @throws IllegalArgumentException FEN is invalid.
     * @param FEN Forsyth-Edwards Notation representation of a draughts position.
     */
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

    /**
     * Gets the player who is moving on this turn.
     * @return "W" or "B" for white and black, respectively.
     */
    public String getActivePlayer() {
        return activePlayer;
    }

    /**
     * Toggles the player who is moving on this turn between white and black.
     */
    public void swapActivePlayer() {
        activePlayer = activePlayer.equals("W") ? "B" : "W";
    }

    public List<Move> generateLegalMoves() {
        List<Move> moves = new ArrayList<>();
        // Generate legal jumps first.
        for (int square = 0; square < state.size(); square++) {
            moves.addAll(generateJumpsFromSquare(square));
        }
        // Jumps are mandatory, so only generate normal moves if
        // there are no jumps.
        if (moves.size() == 0) {
            for (int square = 0; square < state.size(); square++) {
                moves.addAll(generateNormalMovesFromSquare(square));
            }
        }
        return moves;
    }

    protected List<Move> generateJumpsFromSquare(int index) {
        final Map<Piece, Offset[]> pieceToJumpDirections = Map.of(
                Piece.WHITE_MAN, new Offset[]{Offset.JUMP_NORTHEAST, Offset.JUMP_NORTHWEST},
                Piece.BLACK_MAN, new Offset[]{Offset.JUMP_SOUTHEAST, Offset.JUMP_SOUTHWEST},
                Piece.WHITE_KING, new Offset[]{Offset.JUMP_NORTHEAST, Offset.JUMP_NORTHWEST,
                        Offset.JUMP_SOUTHEAST, Offset.JUMP_SOUTHWEST},
                Piece.BLACK_KING, new Offset[]{Offset.JUMP_NORTHEAST, Offset.JUMP_NORTHWEST,
                        Offset.JUMP_SOUTHEAST, Offset.JUMP_SOUTHWEST},
                Piece.NONE, new Offset[]{}
        );
        final Map<Offset, Offset> jumpToMove = Map.of(
                Offset.JUMP_NORTHEAST, Offset.MOVE_NORTHEAST,
                Offset.JUMP_NORTHWEST, Offset.MOVE_NORTHWEST,
                Offset.JUMP_SOUTHEAST, Offset.MOVE_SOUTHEAST,
                Offset.JUMP_SOUTHWEST, Offset.MOVE_SOUTHWEST
        );

        List<Move> moves = new ArrayList<>();
        Piece pieceOnSquare = state.get(index);

        // If the piece is None or if it doesn't belong to the active player,
        // it cannot move. Return the empty move list.
        if (pieceOnSquare == Piece.NONE ||
                (getActivePlayer().equals("W")
                        && (pieceOnSquare == Piece.BLACK_KING || pieceOnSquare == Piece.BLACK_MAN)) ||
                (getActivePlayer().equals("B")
                        && (pieceOnSquare == Piece.WHITE_KING || pieceOnSquare == Piece.WHITE_MAN))) {
            return moves;
        }

        List<Piece> canCapture = new ArrayList<>();
        if (getActivePlayer().equals("W")) {
            canCapture.add(Piece.BLACK_KING);
            canCapture.add(Piece.BLACK_MAN);
        } else {
            canCapture.add(Piece.WHITE_KING);
            canCapture.add(Piece.WHITE_MAN);
        }

        // For every direction this piece could theoretically jump
        for (Offset jumpDirection : pieceToJumpDirections.get(pieceOnSquare)) {
            // Check that jumping would not place the piece out of bounds or on another piece
            int endSquare = addOffsetToSquare(jumpDirection, index);
            if (endSquare == -1 || state.get(endSquare) != Piece.NONE) {
                continue;
            }
            // Check that there is a piece to capture while jumping.
            int jumpedIndex = addOffsetToSquare(jumpToMove.get(jumpDirection), index);
            if (!(canCapture.contains(state.get(jumpedIndex)))) {
                continue;
            }

            // Recursively generate moves for possible continuations after this jump.
            // That is, generate multi-capture, multi-jump moves, provided no
            // promotion occurs on this jump.
            GameState gameStateCopy = new GameState(this);
            // Add one to index since Move uses one-indexed squares.
            Move toMake = new Move(index+1, jumpDirection);
            gameStateCopy.makeMove(toMake);
            boolean multiJumpExists = false;

            if (!((pieceOnSquare == Piece.BLACK_MAN && endSquare >= 28)
                    || (pieceOnSquare == Piece.WHITE_MAN && endSquare <= 3))) {
                for (Move nextJump : gameStateCopy.generateJumpsFromSquare(endSquare)) {
                    // Prepend the current jump to its continuations to create our final move.
                    List<Offset> nextJumpOffsets = nextJump.getOffsets();
                    nextJumpOffsets.add(0, jumpDirection);
                    moves.add(new Move(index + 1, nextJumpOffsets));
                    multiJumpExists = true;
                }
            }

            if (!(multiJumpExists)) {
                moves.add(toMake);
            }
        }
        return moves;
    }

    protected List<Move> generateNormalMovesFromSquare(int index) {
        final Map<Piece, Offset[]> pieceToMoveDirections = Map.of(
                Piece.WHITE_MAN, new Offset[]{Offset.MOVE_NORTHEAST, Offset.MOVE_NORTHWEST},
                Piece.BLACK_MAN, new Offset[]{Offset.MOVE_SOUTHEAST, Offset.MOVE_SOUTHWEST},
                Piece.WHITE_KING, new Offset[]{Offset.MOVE_NORTHEAST, Offset.MOVE_NORTHWEST,
                        Offset.MOVE_SOUTHEAST, Offset.MOVE_SOUTHWEST},
                Piece.BLACK_KING, new Offset[]{Offset.MOVE_NORTHEAST, Offset.MOVE_NORTHWEST,
                        Offset.MOVE_SOUTHEAST, Offset.MOVE_SOUTHWEST},
                Piece.NONE, new Offset[]{}
        );

        List<Move> moves = new ArrayList<>();
        Piece pieceOnSquare = state.get(index);

        // If the piece is None or if it doesn't belong to the active player,
        // it cannot move. Return the empty move list.
        if (pieceOnSquare == Piece.NONE ||
                (getActivePlayer().equals("W")
                        && (pieceOnSquare == Piece.BLACK_KING || pieceOnSquare == Piece.BLACK_MAN)) ||
                (getActivePlayer().equals("B")
                        && (pieceOnSquare == Piece.WHITE_KING || pieceOnSquare == Piece.WHITE_MAN))) {
            return moves;
        }

        for (Offset moveDirection : pieceToMoveDirections.get(pieceOnSquare)) {
            // Check that the destination square isn't occupied or out of bounds.
            int endSquare = addOffsetToSquare(moveDirection, index);
            if (endSquare != -1 && state.get(endSquare) == Piece.NONE) {
                // Add one because Move is one-indexed and this method is zero-indexed.
                moves.add(new Move(index+1, moveDirection));
            }
        }

        return  moves;
    }

    public int countPieces() {
        return state.size() - Collections.frequency(state, Piece.NONE);
    }

    public int makeMove(Move move) {
        // The Move class uses one-indexed squares. state is zero-indexed.
        // Hence, we subtract one.
        int location = move.getStart() - 1;
        Piece moved = state.get(location);
        state.set(location, Piece.NONE);    // Clear the starting square.

        // Clear any jumped pieces. Our strategy: for every jump the moved
        // piece makes, imagine it made a normal move in the same direction
        // instead and clear the square it would land on.
        for (Offset o : move.getOffsets()) {
            switch (o) {
                case JUMP_NORTHEAST:
                    state.set(addOffsetToSquare(Offset.MOVE_NORTHEAST, location), Piece.NONE);
                    break;
                case JUMP_NORTHWEST:
                    state.set(addOffsetToSquare(Offset.MOVE_NORTHWEST, location), Piece.NONE);
                    break;
                case JUMP_SOUTHEAST:
                    state.set(addOffsetToSquare(Offset.MOVE_SOUTHEAST, location), Piece.NONE);
                    break;
                case JUMP_SOUTHWEST:
                    state.set(addOffsetToSquare(Offset.MOVE_SOUTHWEST, location), Piece.NONE);
                    break;
            }
            // Location after this move/jump.
            location = addOffsetToSquare(o, location);
        }

        // Occupy the ending square, promoting as needed.
        if (location >= 28 && moved == Piece.BLACK_MAN) {
            state.set(location, Piece.BLACK_KING);
        } else if (location <= 3 && moved == Piece.WHITE_MAN) {
            state.set(location, Piece.WHITE_KING);
        } else {
            state.set(location, moved);
        }
        // Draughts squares are one-indexed and state squares are zero-indexed, so add one.
        return location+1;
    }

    /**
     * Converts a GameState to a string in Forsyth-Edwards Notation.
     *
     * For more on FENs for draughts,
     * {@see https://en.wikipedia.org/wiki/Portable_Draughts_Notation#Tag_Pairs}.
     * This method orders pieces by square from lowest to highest, so expect
     * "W:WK1,5,7:B18,22" and not "W:W5,7,K1:B22,18".
     *
     * @return Forsyth-Edwards Notation representation of this position.
     */
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

    /**
     * Finds the end square for a given move type from a given square.
     *
     * This method accounts for idiosyncrasies in square numbering. Illegal inputs
     * (for example, those that would move a piece off the board) return -1.
     *
     * @param offset Type of move to make.
     * @param index Square to make move from, represented by an index into state.
     * @return End square after the move, represented by an index into state.
     */
    protected static int addOffsetToSquare(Offset offset, int index) {
        int row = index/4;
        int resultantSquare;
        switch (offset) {
            case MOVE_NORTHEAST:
                resultantSquare = index - (row%2 == 0 ? 3 : 4);
                break;
            case MOVE_NORTHWEST:
                resultantSquare = index - (row%2 == 0 ? 4 : 5);
                break;
            case MOVE_SOUTHEAST:
                resultantSquare = index + (row%2 == 0 ? 5 : 4);
                break;
            case MOVE_SOUTHWEST:
                resultantSquare = index + (row%2 == 0 ? 4 : 3);
                break;
            case JUMP_NORTHEAST:
                resultantSquare = index-7;
                break;
            case JUMP_NORTHWEST:
                resultantSquare = index-9;
                break;
            case JUMP_SOUTHEAST:
                resultantSquare = index+9;
                break;
            case JUMP_SOUTHWEST:
                resultantSquare = index+7;
                break;
            default:
                return -1;
        }

        if (resultantSquare < 0 || resultantSquare > 31) {
            // Detect out-of-bounds rows.
            return -1;
        } else if (Math.abs(resultantSquare%4 - index%4) > 1) {
            // Detect out-of-bounds columns by checking for wrapping
            // across the board.
            return -1;
        } else {
            return resultantSquare;
        }
    }
}
