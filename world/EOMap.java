package com.endlessonline.client.world;

import com.endlessonline.client.io.EOReader;
import com.endlessonline.client.io.EOSerializer;

import java.util.*;

public class EOMap {

    private int rid;
    private int fileSize;
    private String name = "";
    private Type type = Type.DEFAULT;
    private int width;
    private int height;
    private int fill;
    private int control;
    private int mfx;
    private int sfx;
    private boolean pkEnabled;
    private boolean miniMapEnabled;
    private boolean scrollsEnabled;
    private Tile[][] tiles;

    public void load(byte[] bytes) {
        this.load(new EOReader(bytes));
    }

    public void load(EOReader reader) {
        this.fileSize = reader.available();
        reader.skip(3);
        this.rid = reader.readInt();
        this.name = EOSerializer.decodeString(reader.readFixedString(24));
        this.pkEnabled = reader.readChar() > 0;
        this.type = Type.valueOf(reader.readChar());
        this.mfx = reader.readChar();
        this.control = reader.readChar();
        this.sfx = reader.readShort();
        this.width = reader.readChar() + 1;
        this.height = reader.readChar() + 1;
        this.fill = reader.readShort();
        this.miniMapEnabled = reader.readChar() > 0;
        this.scrollsEnabled = reader.readChar() > 0;
        reader.skip(3);

        this.tiles = new Tile[this.height][this.width];
        for (int y=0; y<this.height; y++) {
            for (int x=0; x<this.width; x++) {
                this.tiles[y][x] = new Tile(x, y);
            }
        }

        this.readNPCSpawns(reader);
        this.readUnknowns(reader);
        this.readItemSpawns(reader);
        this.readSpecs(reader);
        this.readWarps(reader);
        this.readLayer(Layer.GROUND, reader);
        this.readLayer(Layer.OBJECTS, reader);
        this.readLayer(Layer.OVERLAYS_1, reader);
        this.readLayer(Layer.DOWN_WALLS, reader);
        this.readLayer(Layer.RIGHT_WALLS, reader);
        this.readLayer(Layer.ROOFS, reader);
        this.readLayer(Layer.TOPS, reader);
        this.readLayer(Layer.SHADOWS, reader);
        this.readLayer(Layer.OVERLAYS_2, reader);
        this.readSigns(reader);
    }

    private void readNPCSpawns(EOReader reader) {
        for (int i=0, total=reader.readChar(); i<total; i++) {
            reader.skip(8);
        }
    }

    private void readUnknowns(EOReader reader) {
        for (int i=0, total=reader.readChar(); i<total; i++) {
            reader.skip(4);
        }
    }

    private void readItemSpawns(EOReader reader) {
        for (int i=0, total=reader.readChar(); i<total; i++) {
            reader.skip(12);
        }
    }

    private void readSpecs(EOReader reader) {
        for (int j=0, rows=reader.readChar(); j<rows; j++) {
            int y = reader.readChar();
            for (int i=0, columns=reader.readChar(); i<columns; i++) {
                int x = reader.readChar();
                int id = reader.readChar();
                if (this.isWithinBounds(x, y)) {
                    this.tiles[y][x].spec = Spec.valueOf(id);
                }
            }
        }
    }

    private void readWarps(EOReader reader) {
        for (int j=0, rows=reader.readChar(); j<rows; j++) {
            int y = reader.readChar();
            for (int i=0, columns=reader.readChar(); i<columns; i++) {
                int x = reader.readChar();
                int warpMap = reader.readShort();
                int warpX = reader.readChar();
                int warpY = reader.readChar();
                int level = reader.readChar();
                int key = reader.readShort();
                if (this.isWithinBounds(x, y)) {
                    this.tiles[y][x].warp = new Warp(warpMap, warpX, warpY, level, key);
                }
            }
        }
    }

    private void readLayer(Layer layer, EOReader reader)  {
        for (int j=0, rows=reader.readChar(); j<rows; j++) {
            int y = reader.readChar();
            for (int i=0, columns=reader.readChar(); i<columns; i++) {
                int x = reader.readChar();
                int id = reader.readShort();
                if (this.isWithinBounds(x, y)) {
                    this.tiles[y][x].graphics.put(layer, id);
                }
            }
        }
    }

    private void readSigns(EOReader reader) {
        if (reader.available() > 0) {
            for (int i=0, totalSigns=reader.readChar(); i<totalSigns; i++) {
                int x = reader.readChar();
                int y = reader.readChar();
                int length = reader.readShort() - 1;
                String data = EOSerializer.decodeString(reader.readFixedString(length));
                int titleLength = reader.readChar();
                if (this.isWithinBounds(x, y)) {
                    this.tiles[y][x].sign = new Sign(data.substring(0, titleLength), data.substring(titleLength));
                }
            }
        }
    }

    public int getRID() {
        return this.rid;
    }

    public boolean isOutdated(int hash, int size) {
        return this.rid != hash || this.fileSize != size;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < this.width && y >= 0 && y < this.height;
    }

    public int getFill() {
        return this.fill;
    }

    public int getControl() {
        return this.control;
    }

    public int getMFX() {
        return this.mfx;
    }

    public int getSFX() {
        return this.sfx;
    }

    public boolean isPKEnabled() {
        return this.pkEnabled;
    }

    public boolean isMiniMapEnabled() {
        return this.miniMapEnabled;
    }

    public boolean isScrollsEnabled() {
        return this.scrollsEnabled;
    }

    public Tile getTile(int x, int y) {
        return this.tiles[y][x];
    }

    public void clear() {
        for (int y=0; y<this.height; y++) {
            for(int x=0; x<this.width; x++) {
                this.tiles[y][x].clear();
            }
        }
    }

    public enum Type {
        DEFAULT,
        HEALTH_DRAIN,
        MANA_DRAIN,
        QUAKE_1,
        QUAKE_2,
        QUAKE_3,
        QUAKE_4;

        public static Type valueOf(int id) {
            return Type.values()[id % Type.values().length];
        }
    }

    public static class Tile {

        private final int x, y;
        private final Map<Layer, Integer> graphics = new EnumMap<>(Layer.class);
        private final Collection<Item> items = new LinkedHashSet<>();
        private final Collection<Character> characters = new LinkedHashSet<>();
        private final Collection<NPC> npcs = new LinkedHashSet<>();
        private Spec spec = Spec.NONE;
        private Warp warp;
        private Sign sign;

        public Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getGraphic(Layer layer) {
            return this.graphics.getOrDefault(layer, 0);
        }

        public boolean hasItem() {
            Iterator<Item> iterator = items.iterator();
            if (iterator.hasNext()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean hasNpc() {
            Iterator<NPC> iterator = npcs.iterator();
            if (iterator.hasNext()) {
                return true;
            } else {
                return false;
            }
        }

        public boolean hasChar() {
            Iterator<Character> iterator = characters.iterator();
            if (iterator.hasNext()) {
                return true;
            } else {
                return false;
            }
        }

        public Character getChar(int uid) {
            Map<Integer, Character> chars = new HashMap();
            Iterator<Character> iterator = characters.iterator();
            while (iterator.hasNext()) {
                Character _char = iterator.next().getChar();
                chars.put(_char.getUID(), _char.getChar());
            }

            return chars.get(uid);
        }

        public Character getFirstChar() {
            Iterator<Character> iterator = characters.iterator();
            return iterator.next();
        }

        public Collection<Item> getItems() {
            return this.items;
        }

        public Collection<Character> getChars() {
            return this.characters;
        }

        public Collection<NPC> getNpcs() {
            return this.npcs;
        }

        public Integer hasGraphic(Layer layer) {
            return this.graphics.get(layer);
        }

        public void add(Item item) {
            this.items.add(item);
        }

        public void add(Character character) {
            this.characters.add(character);
        }

        public void add(NPC npc) {
            this.npcs.add(npc);
        }

        public void remove(Item item) {
            this.items.remove(item);
        }

        public void remove(Character character) {
            this.characters.remove(character);
        }

        public void remove(NPC npc) {
            this.npcs.remove(npc);
        }

        public void clear() {
            this.items.clear();
            this.characters.clear();
            this.npcs.clear();
        }

        public boolean hasWarp() {
            return this.warp != null;
        }

        public Warp getWarp() {
            return this.warp;
        }

        public Spec getSpec() {
            return this.spec;
        }

        public boolean hasSign() {
            return this.sign != null;
        }

        public Sign getSign() {
            return this.sign;
        }

        public boolean isWalkable() {
            return !this.hasWarp() && this.characters.isEmpty() && this.spec.isWalkable();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tile tile = (Tile) o;

            if (x != tile.x) return false;
            return y == tile.y;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public String toString() {
            return "Tile{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    public enum Layer {
        GROUND,
        SHADOWS,
        OBJECTS,
        DOWN_WALLS,
        RIGHT_WALLS,
        TOPS,
        ROOFS,
        OVERLAYS_1,
        OVERLAYS_2,
    }

    public enum Spec {
        NONE(-1, true),
        WALL(0, false),
        CHAIR_DOWN(1, false),
        CHAIR_LEFT(2, false),
        CHAIR_RIGHT(3, false),
        CHAIR_UP(4, false),
        CHAIR_DOWN_RIGHT(5, false),
        CHAIR_UP_LEFT(6, false),
        CHAIR_ALL(7, false),
        CHEST(9, false),
        BANK_VAULT(16, false),
        NPC_BOUNDARY(17, true),
        EDGE(18, false),
        FAKE_WALL(19, true),
        BOARD_1(20, false),
        BOARD_2(21, false),
        BOARD_3(22, false),
        BOARD_4(23, false),
        BOARD_5(24, false),
        BOARD_6(25, false),
        BOARD_7(26, false),
        BOARD_8(27, false),
        JUKEBOX(28, false),
        JUMP(29, true),
        WATER(30, true),
        ARENA(32, true),
        AMBIENT_SOURCE(33, true),
        TIMED_SPIKE(34, true),
        SPIKE(35, true),
        TRIGGERED_SPIKE(36, true);

        private final int id;
        private final boolean walkable;

        Spec(int id, boolean walkable) {
            this.id = id;
            this.walkable = walkable;
        }

        public boolean isWalkable() {
            return this.walkable;
        }

        private static final Map<Integer, Spec> TABLE = new HashMap<>(Spec.values().length);
        static {
            for (Spec spec : Spec.values()) {
                TABLE.put(spec.id, spec);
            }
        }

        public static Spec valueOf(int id) {
            return TABLE.getOrDefault(id, NONE);
        }
    }

    public static class Sign {

        private final String title;
        private final String message;

        public Sign(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public String getTitle() {
            return this.title;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static class Warp {

        private final int map, x, y;
        private final int levelRequired, keyRequired;

        public Warp(int map, int x, int y, int levelRequired, int keyRequired) {
            this.map = map;
            this.x = x;
            this.y = y;
            this.levelRequired = levelRequired;
            this.keyRequired = keyRequired;
        }

        public int getMap() {
            return this.map;
        }

        public int getX()       {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getLevelRequired() {
            return this.levelRequired;
        }

        public int getKeyRequired() {
            return this.keyRequired;
        }
    }
}