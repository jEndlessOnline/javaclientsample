package com.endlessonline.client.io;

public enum ENF {

    GFX_ID,
    TYPE,
    BOSS,
    CHILD,
    HEALTH,
    VENDOR,
    MINIMUM_DAMAGE,
    MAXIMUM_DAMAGE,
    ACCURACY,
    EVASION,
    DEFENSE,
    EXPERIENCE;

    public enum Type {

        NON_COMBAT(0),
        PASSIVE(1),
        AGGRESSIVE(2),
        SHOP(6),
        INN(7),
        BANK(9),
        BARBER(10),
        GUILD(11),
        PRIEST(12),
        LAW(13),
        SKILL_MASTER(14),
        QUEST(15);

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

            return NON_COMBAT;
        }
    }
}
