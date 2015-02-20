package fr.quentinmachu.infernalmaze.maze;

import java.util.concurrent.ThreadLocalRandom;

public enum Direction {
    NORTH(1, 0, -1) { @Override public Direction oppositeDirection() { return SOUTH; }},
    SOUTH(2, 0, 1) { @Override public Direction oppositeDirection() { return NORTH; }},
    WEST(4, -1, 0) { @Override public Direction oppositeDirection() { return EAST; }},
    EAST(8, 1, 0) { @Override public Direction oppositeDirection() { return WEST; }};
    
    private static final ThreadLocalRandom rnd = ThreadLocalRandom.current();
    public final int bit, dx, dy;
    
    private Direction(int ordinal, int dx, int dy) {
        this.bit = ordinal;
        this.dx = dx;
        this.dy = dy;
    }
    
    static public Direction randomDirection() {
        return Direction.values()[rnd.nextInt(4)];
    }
    
    abstract public Direction oppositeDirection();
}
