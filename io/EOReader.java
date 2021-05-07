package com.endlessonline.client.io;

import java.util.ArrayList;
import java.util.List;

public class EOReader {

    private final List<Byte> data;
    private int index;

    public EOReader(byte[] data) {
        this.data = new ArrayList<>(data.length);
        for (byte value : data) {
            this.data.add(value);
        }
    }

    public EOReader(List<Byte> data) {
        this.data = data;
    }

    public static EOReader fromNetwork(List<Byte> data) {
        EOReader reader = new EOReader(data);
        reader.index = 2;
        return reader;
    }

    public EOAction getAction() {
        int id = this.data.size() > 0 ? (this.data.get(0) & 0xFF) : 0;
        return EOAction.valueOf(id);
    }

    public EOFamily getFamily() {
        int id = this.data.size() > 1 ? (this.data.get(1) & 0xFF) : 0;
        return EOFamily.valueOf(id);
    }

    public int readByte() {
        if (this.index >= this.data.size()) {
            return 0;
        }

        return this.data.get(this.index++) & 0xFF;
    }

    public int readChar() {
        int a = this.readByte();
        return EOSerializer.bytesToNumber(a);
    }

    public int readShort() {
        int a = this.readByte();
        int b = this.readByte();
        return EOSerializer.bytesToNumber(a, b);
    }

    public int readThree() {
        int a = this.readByte();
        int b = this.readByte();
        int c = this.readByte();
        return EOSerializer.bytesToNumber(a, b, c);
    }

    public int readInt() {
        int a = this.readByte();
        int b = this.readByte();
        int c = this.readByte();
        int d = this.readByte();
        return EOSerializer.bytesToNumber(a, b, c, d);
    }

    public byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        length = Math.min(length, this.available());
        for (int i=0; i<length; i++) {
            bytes[i] = this.data.get(this.index + i);
        }

        this.index += length;
        return bytes;
    }

    public byte[] readEndBytes() {
        return this.readBytes(this.available());
    }

    public String readFixedString(int length) {
        StringBuilder builder = new StringBuilder(length);
        for(int i=0; i<length; i++) {
            builder.append((char)this.readByte());
        }

        return builder.toString();
    }

    public String readEndString() {
        return this.readFixedString(this.available());
    }

    public String readBreakString() {
        StringBuilder builder = new StringBuilder();
        for (int next=this.readByte(); this.available() > 0 && next != 0xFF; next=this.readByte()) {
            builder.append((char)next);
        }

        return builder.toString();
    }

    public void skip(int l) {
        this.index = Math.min(this.index + l, this.data.size());
    }

    public int available() {
        return this.data.size() - this.index;
    }

    public int peek() {
        if (this.available() <= 0) return 0;
        return this.data.get(this.index) & 0xFF;
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}