package com.endlessonline.client.io;

public enum ESF {

    ICON,
    GFX_ID,
    TYPE,
    TARGET_TYPE,
    TARGET_RESTRICT,

    MANA_COST,
    STAMINA_COST,
    CAST_TIME,

    MINIMUM_DAMAGE,
    MAXIMUM_DAMAGE,
    ACCURACY,
    HEALTH;

    public enum Type {

        HEAL(0),
        DAMAGE(1),
        BARD(2);

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

            return HEAL;
        }
    }

    public enum TargetType {

        NORMAL(0),
        SELF(1),
        GROUP(3);

        private final int id;

        TargetType(int id) {
            this.id = id;
        }

        public int value() {
            return this.id;
        }

        public static TargetType valueOf(int id) {
            for (TargetType targetType : TargetType.values()) {
                if (id == targetType.id) {
                    return targetType;
                }
            }

            return NORMAL;
        }
    }

    public enum TargetRestrict {

        NPC(0),
        FRIENDLY(1),
        OPPONENT(2);

        private final int id;

        TargetRestrict(int id) {
            this.id = id;
        }

        public int value() {
            return this.id;
        }

        public static TargetRestrict valueOf(int id) {
            for (TargetRestrict targetRestrict : TargetRestrict.values()) {
                if (id == targetRestrict.id) {
                    return targetRestrict;
                }
            }

            return NPC;
        }
    }
}
