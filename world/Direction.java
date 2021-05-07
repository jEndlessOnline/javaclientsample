package com.endlessonline.client.world;

public enum Direction {

    DOWN,
    LEFT,
    UP,
    RIGHT;

    public static Direction valueOf(int id) {
        return Direction.values()[id % Direction.values().length];
    }
}
