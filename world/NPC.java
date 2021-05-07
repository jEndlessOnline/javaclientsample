package com.endlessonline.client.world;

import com.endlessonline.client.io.EOReader;

public class NPC {

    private final int uid;
    private final int id;
    private int x;
    private int y;
    private Direction direction;

    public NPC(EOReader reader) {
        this.uid = reader.readChar();
        this.id = reader.readShort();
        this.x = reader.readChar();
        this.y = reader.readChar();
        this.direction = Direction.valueOf(reader.readChar());
    }

    public int getUID() {
        return this.uid;
    }

    public int getID() {
        return this.id;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void walk(Direction direction, int x, int y) {
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    public void attack(Direction direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NPC npc = (NPC) o;
        return uid == npc.uid;
    }

    @Override
    public int hashCode() {
        return this.uid;
    }

    @Override
    public String toString() {
        return "NPC{" +
                "uid=" + uid +
                ", id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", direction=" + direction +
                '}';
    }
}
