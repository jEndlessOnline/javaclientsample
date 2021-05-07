package com.endlessonline.client.io;

public enum EIF {

    ICON,
    TYPE,
    SUB_TYPE,
    QUALITY,
    HEALTH,
    MANA,
    MINIMUM_DAMAGE,
    MAXIMUM_DAMAGE,
    ACCURACY,
    EVASION,
    DEFENSE,
    STRENGTH,
    INTELLIGENCE,
    WISDOM,
    AGILITY,
    CONSTITUTION,
    CHARISMA,
    SCROLL_MAP,
    GFX_ID,
    EXP_REWARD,
    HAIR_COLOR,
    EFFECT,
    KEY,
    GENDER,
    SCROLL_X,
    SCROLL_Y,
    LEVEL_REQUIRED,
    CLASS_REQUIRED,
    STRENGTH_REQUIRED,
    INTELLIGENCE_REQUIRED,
    WISDOM_REQUIRED,
    AGILITY_REQUIRED,
    CONSTITUTION_REQUIRED,
    CHARISMA_REQUIRED,
    WEIGHT,
    SIZE;

    public enum Type {

        STATIC(0),
        MONEY(2),
        HEAL(3),
        TELEPORT(4),
        SPELL(5),
        EXP_REWARD(6),
        STAT_REWARD(7),
        SKILL_REWARD(8),
        KEY(9),
        WEAPON(10),
        OFF_HAND(11),
        ARMOR(12),
        HAT(13),
        BOOTS(14),
        GLOVES(15),
        ACCESSORY(16),
        BELT(17),
        NECKLACE(18),
        RING(19),
        ARMLET(20),
        BRACER(21),
        ALCOHOL(22),
        EFFECT(23),
        HAIR_DYE(24),
        CURSE_CURE(25);

        private final int id;

        Type(int id) {

            this.id = id;
        }

        public int value() {

            return this.id;
        }

        public static Type valueOf(int id) {

            for (Type type : Type.values()) {
                if (id == type.id) {
                    return type;
                }
            }

            return STATIC;
        }
    }

    public enum SubType {

        NONE(0),
        RANGED(1),
        QUIVER(2),
        WINGS(3),
        TWO_HANDED(4);

        private final int id;

        SubType(int id) {

            this.id = id;
        }

        public int value() {

            return this.id;
        }

        public static SubType valueOf(int id) {

            for (SubType subType : SubType.values()) {

                if (id == subType.id) {
                    return subType;
                }
            }

            return NONE;
        }
    }

    public enum Quality {

        NORMAL(0),
        RARE(1),
        UNIQUE(3),
        LORE(4),
        CURSED(5);

        private final int id;

        Quality(int id) {

            this.id = id;
        }

        public int value() {

            return this.id;
        }

        public static Quality valueOf(int id) {

            for (Quality quality : Quality.values()) {
                if (id == quality.id) {
                    return quality;
                }
            }

            return NORMAL;
        }
    }

    public enum Size {

        _1X1(0),
        _1X2(1),
        _1X3(2),
        _1X4(3),
        _2X1(4),
        _2X2(5),
        _2X3(6),
        _2X4(7);

        private final int id;

        Size(int id) {

            this.id = id;
        }

        public int value() {

            return this.id;
        }

        public static Size valueOf(int id) {

            for (Size size : Size.values()) {
                if (id == size.id) {
                    return size;
                }
            }

            return _1X1;
        }
    }
}
