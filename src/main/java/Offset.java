public enum Offset {
    MOVE_SOUTHEAST(5),
    MOVE_SOUTHWEST(4),
    MOVE_NORTHEAST(-MOVE_SOUTHWEST.offset),
    MOVE_NORTHWEST(-MOVE_SOUTHEAST.offset),
    JUMP_SOUTHEAST(9),
    JUMP_SOUTHWEST(7),
    JUMP_NORTHEAST(-JUMP_SOUTHWEST.offset),
    JUMP_NORTHWEST(-JUMP_SOUTHEAST.offset);

    private final int offset;

    private Offset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
