package com.endlessonline.client.io;

import java.util.HashMap;
import java.util.Map;

public enum EOAction {
    INTERNAL(0),
    REQUEST(1),
    ACCEPT(2),
    REPLY(3),
    REMOVE(4),
    AGREE(5),
    CREATE(6),
    ADD(7),
    PLAYER(8),
    TAKE(9),
    USE(10),
    BUY(11),
    SELL(12),
    OPEN(13),
    CLOSE(14),
    MESSAGE(15),
    SPEC(16),
    ADMIN(17),
    LIST(18),
    TELL(20),
    REPORT(21),
    ANNOUNCE(22),
    SERVER(23),
    DROP(24),
    JUNK(25),
    OBTAIN(26),
    GET(27),
    KICK(28),
    RANK(29),
    TARGET_SELF(30),
    TARGET_OTHER(31),
    TARGET_GROUP(33),

    PING(240),
    PONG(241),
    NET_3(242),

    INIT(255);

    private static final Map<Integer, EOAction> TABLE = new HashMap<>(EOAction.values().length);
    static {
        for (EOAction action : EOAction.values()) {
            TABLE.put(action.value(), action);
        }
    }

    private final int id;

    EOAction(int id) {
        this.id = id;
    }

    public int value() {
        return this.id;
    }

    public static EOAction valueOf(int id) {
        return TABLE.getOrDefault(id, INTERNAL);
    }
}
