package com.endlessonline.client.io;

import java.util.ArrayList;
import java.util.List;

public class EOWriter {

    private final List<Byte> data;

    public EOWriter(int size) {
        this.data = new ArrayList<>(size);
    }

    public EOWriter(EOAction action, EOFamily family, int size) {
        this(2 + size);
        this.writeByte(action.value());
        this.writeByte(family.value());
    }

    public EOWriter writeByte(int number) {
        this.data.add((byte)number);
        return this;
    }

    private EOWriter writeNumber(int number, int length) {
        byte[] values = EOSerializer.numberToBytes(number, length);
        for (byte value : values) {
            this.data.add(value);
        }

        return this;
    }

    public EOWriter writeChar(int number) {
       return this.writeNumber(number, 1);
    }

    public EOWriter writeShort(int number) {
        return this.writeNumber(number, 2);
    }

    public EOWriter writeThree(int number) {
        return this.writeNumber(number, 3);
    }

    public EOWriter writeInt(int number) {
        return this.writeNumber(number, 4);
    }

    public EOWriter writeFixedString(String string) {
        for(byte b : string.getBytes()) {
            this.writeByte(b);
        }

        return this;
    }

    public EOWriter writeBreakString(String string) {
        this.writeFixedString(string);
        this.writeByte(0xFF);
        return this;
    }

    public List<Byte> getData() {
        return this.data;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}