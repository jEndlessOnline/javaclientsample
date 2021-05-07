package com.endlessonline.client.world;

import com.endlessonline.client.io.EOSerializer;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Player {

    private static final long[] EXPERIENCE_TABLE = new long[EOSerializer.ONE_BYTE_MAX + 1];
    static {
        for (int i=0; i<EXPERIENCE_TABLE.length; i++) {
            EXPERIENCE_TABLE[i] = (long)Math.floor(Math.pow(i, 3) * 133.1);
        }
    }

    public static long totalExperience(int level) {
        return EXPERIENCE_TABLE[level];
    }

    private int uid;
    private final Map<Stat, Integer> stats = new EnumMap<>(Stat.class);
    private final Map<Integer, Item> items = new HashMap<>();
    private final Map<Integer, Spell> spells = new HashMap<>();
    private String name = "";
    private String title = "";
    private String guildName = "";
    private String guildRankName = "";
    private String guildTag = "";

    public int getUID() {
        return this.uid;
    }

    public void setUID(int uid) {
        this.uid = uid;
    }

    public int getStat(Stat stat) {
        return this.stats.getOrDefault(stat, 0);
    }

    public void setStat(Stat stat, int value) {
        this.stats.put(stat, value);
    }

    public long getRequiredExperience() {
        return totalExperience(this.getStat(Stat.LEVEL) + 1) - totalExperience(this.getStat(Stat.LEVEL));
    }

    public long getLevelExperience() {
        return this.getStat(Stat.EXPERIENCE) - totalExperience(this.getStat(Stat.LEVEL));
    }

    public void addItem(int id, int amount) {

    }

    public void removeItem(int id, int amount) {

    }

    public void setSpell(int id, int level) {
        if (this.spells.containsKey(id)) {
            this.spells.get(id).level = level;
        }  else {
            this.spells.put(id, new Spell(id, level));
        }
    }

    public void removeSpell(int id) {
        this.spells.remove(id);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGuildName() {
        return this.guildName;
    }

    public void setGuildName(String guildName) {
        this.guildName = guildName;
    }

    public String getGuildRankName() {
        return this.guildRankName;
    }

    public void setGuildRankName(String guildRankName) {
        this.guildRankName = guildRankName;
    }

    public String getGuildTag() {
        return this.guildTag;
    }

    public void setGuildTag(String guildTag) {
        this.guildTag = guildTag;
    }

    public boolean isOverweight() {
        return this.getStat(Stat.WEIGHT) > this.getStat(Stat.MAX_WEIGHT);
    }

    @Override
    public String toString() {
        return "Player{" +
                "uid=" + uid +
                ", stats=" + stats +
                ", items=" + items +
                ", spells=" + spells +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", guildName='" + guildName + '\'' +
                ", guildRankName='" + guildRankName + '\'' +
                ", guildTag='" + guildTag + '\'' +
                '}';
    }

    public enum Stat {
        MAP,
        CLASS,
        ADMIN,
        LEVEL,
        EXPERIENCE,
        USAGE, // minutes
        HEALTH,
        MAX_HEALTH,
        MANA,
        MAX_MANA,
        STAMINA,
        MAX_STAMINA,
        WEIGHT,
        MAX_WEIGHT,
        STAT_POINTS,
        SKILL_POINTS,
        KARMA,
        MIN_DAMAGE,
        MAX_DAMAGE,
        ACCURACY,
        EVASION,
        DEFENSE,
        STRENGTH,
        INTELLIGENCE,
        WISDOM,
        AGILITY,
        CONSTITUTION,
        CHARISMA,
        GUILD_RANK,
        JAIL,
    }

    public static class Item {

        private final int id;
        private final int amount;

        public Item(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        public int getID() {
            return this.id;
        }

        public int getAmount() {
            return this.amount;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "id=" + id +
                    ", amount=" + amount +
                    '}';
        }
    }

    public static class Spell {

        private final int id;
        private int level;

        public Spell(int id, int level) {
            this.id = id;
            this.level = level;
        }

        public int getID() {
            return this.id;
        }

        public int getLevel() {
            return this.level;
        }

        @Override
        public String toString() {
            return "Spell{" +
                    "id=" + id +
                    ", level=" + level +
                    '}';
        }
    }
}
