package com.endlessonline.client.world;

import com.endlessonline.client.io.EOReader;

public class Item {

    private final int uid;
    private final int id;
    private final int amount;
    private final int x;
    private final int y;

    public Item(int uid, int id, int x, int y, int amount) {
        this.uid = uid;
        this.id = id;
        this.x = x;
        this.y = y;
        this.amount = amount;
    }

    public Item(EOReader reader) {
        this.uid = reader.readShort();
        this.id = reader.readShort();
        this.x = reader.readChar();
        this.y = reader.readChar();
        this.amount = reader.readThree();
    }

    public int getUID() {
        return this.uid;
    }

    public int getID() {
        return this.id;
    }

    public int getAmount() {
        return this.amount;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;
        return uid == item.uid;
    }

    @Override
    public int hashCode() {
        return uid;
    }

    @Override
    public String toString() {
        return "Item{" +
                "uid=" + uid +
                ", id=" + id +
                ", amount=" + amount +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
