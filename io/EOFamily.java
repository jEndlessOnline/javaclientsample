package com.endlessonline.client.io;

import java.util.HashMap;
import java.util.Map;

public enum EOFamily {

    INTERNAL(0),
    CONNECTION(1),
    ACCOUNT(2),
    CHARACTER(3),
    LOGIN(4),
    WELCOME(5),
    WALK(6),
    FACE(7),
    CHAIR(8),
    EMOTE(9),
    ATTACK(11),
    SPELL(12),
    SHOP(13),
    ITEM(14),
    STAT_SKILL(16),
    GLOBAL(17),
    TALK(18),
    WARP(19),
    JUKEBOX(21),
    PLAYERS(22),
    AVATAR(23),
    PARTY(24),
    REFRESH(25),
    NPC(26),
    AUTO_REFRESH(27),
    AUTO_REFRESH_2(28),
    APPEAR(29),
    PAPERDOLL(30),
    EFFECT(31),
    TRADE(32),
    CHEST(33),
    DOOR(34),
    MESSAGE(35),
    BANK(36),
    LOCKER(37),
    BARBER(38),
    GUILD(39),
    MUSIC(40),
    SIT(41),
    RECOVER(42),
    BOARD(43),
    CAST(44),
    ARENA(45),
    PRIEST(46),
    MARRIAGE(47),
    ADMIN_INTERACT(48),
    CITIZEN(49),
    QUEST(50),
    BOOK(51),
    INIT(255);

    private static final Map<Integer, EOFamily> TABLE = new HashMap<>(EOFamily.values().length);
    static {
        for (EOFamily family : EOFamily.values()) {
            TABLE.put(family.value(), family);
        }
    }

    private final int id;

    EOFamily(int id) {
        this.id = id;
    }

    public int value() {
        return this.id;
    }

    public static EOFamily valueOf(int id) {
        return TABLE.getOrDefault(id, INTERNAL);
    }
}
