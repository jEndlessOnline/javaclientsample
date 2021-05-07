package com.endlessonline.client.world;

public enum Emote {

    UNKNOWN(0),
    HAPPY(1),
    DEPRESSED(2),
    SAD(3),
    ANGRY(4),
    CONFUSED(5),
    SURPRISED(6),
    HEARTS(7),
    DREAMY(8),
    SUICIDAL(9),
    EMBARRASSED(10),
    DRUNK(11),
    TRADE(12),
    LEVEL_UP(13),
    PLAYFUL(14);

    private final int id;

    Emote(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static Emote valueOf(int id) {
        for (Emote emote : Emote.values()) {
            if (id == emote.id) {
                return emote;
            }
        }

        return UNKNOWN;
    }
}
