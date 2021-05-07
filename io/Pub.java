package com.endlessonline.client.io;

import java.util.*;

public abstract class Pub<T> implements Iterable<Pub.Entry<T>> {

    private int rid = 0;
    private final List<Entry<T>> entries = new ArrayList<>();

    public boolean isOutdated(int hash, int size) {
        return this.rid != hash || this.entries.size() != size;
    }

    public void load(byte[] bytes) {
        List<Byte> data = new ArrayList<>(bytes.length);
        for (byte b : bytes) {
            data.add(b);
        }

        this.load(new EOReader(data));
    }

    public void load(EOReader reader) {
        String magic = reader.readFixedString(3);
        this.entries.clear();
        this.rid = reader.readInt();
        int total = reader.readShort();
        reader.skip(1);
        for (int i=0; i<total; i++) {
            Entry<T> entry = this.unserializeEntry(reader);
            this.entries.add(entry);
        }
    }

    protected abstract void serializeEntry(EOWriter writer, Entry<T> entry);

    protected abstract Entry<T> unserializeEntry(EOReader reader);

    public boolean hasEntry(int id) {
        return id > 0 && id <= this.entries.size();
    }

    public Entry<T> getEntry(int id) {
        return this.hasEntry(id) ? this.entries.get(id - 1) : this.entries.get(0);
    }

    @Override
    public Iterator<Entry<T>> iterator() {
        return this.entries.iterator();
    }

    public static class Entry<T> {

        private String name = "";
        private String flavorText = "";
        private final Map<T, Integer> properties = new HashMap<>();

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFlavorText() {
            return this.flavorText;
        }

        public void setFlavorText(String flavorText) {
            this.flavorText = flavorText;
        }

        public int getProperty(T property) {
            return this.properties.getOrDefault(property, 0);
        }

        public void setProperty(T property, int value) {
            this.properties.put(property, value);
        }

        @Override
        public String toString() {
            return String.format(Locale.US, "%s: %s", this.name, this.properties);
        }
    }
}
