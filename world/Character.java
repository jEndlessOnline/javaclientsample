package com.endlessonline.client.world;

import com.endlessonline.client.io.EOReader;

public class Character {

    private final int uid;
    private final int map;
    private int x;
    private int y;
    private Direction direction;
    private final String name;
    private final String guildTag;
    private final Gender gender;
    private final int skin;
    private final int hairStyle;
    private final int hairColor;
    private final int boots;
    private final int armor;
    private final int hat;
    private final int weapon;
    private final int shield;
    private final int level;
    private final int classification;
    private final int health;
    private final int maxHealth;
    private final int mana;
    private final int maxMana;
    private boolean invisible;
    private State state;

    public Character(EOReader reader) {
        this.name = reader.readBreakString();
        this.uid = reader.readShort();
        this.map = reader.readShort();
        this.x = reader.readShort();
        this.y = reader.readShort();
        this.direction = Direction.valueOf(reader.readChar());
        this.classification = reader.readChar();
        this.guildTag = reader.readFixedString(3);
        this.level = reader.readChar();
        this.gender = Gender.valueOf(reader.readChar());
        this.hairStyle = reader.readChar();
        this.hairColor = reader.readChar();
        this.skin = reader.readChar();
        this.maxHealth = reader.readShort();
        this.health = reader.readShort();
        this.maxMana = reader.readShort();
        this.mana = reader.readShort();
        this.boots = reader.readShort(); reader.skip(6);
        this.armor = reader.readShort(); reader.skip(2);
        this.hat = reader.readShort();
        this.shield = reader.readShort();
        this.weapon = reader.readShort();
        this.state = State.valueOf(reader.readChar());
        this.invisible = reader.readChar() > 0;
        reader.readBreakString();
    }

    public int getGenderVal() {
        if (this.gender == Gender.FEMALE) {
            return 0;
        } else if (this.gender == Gender.MALE) {
            return 1;
        } else {
            return 1;
        }
    }

    public Character getChar() { return this; }

    public int getUID() {
        return this.uid;
    }

    public int getArmor() { return this.armor; }

    public int getWeapon() { return this.weapon; }

    public int getBoots() { return this.boots; }

    public int getHat() { return this.hat; }

    public int getShield() { return this.shield; }

    public int getMap() {
        return this.map;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getName() {
        return this.name;
    }

    public Gender getGender() { return this.gender; }

    public String getGuildTag() {
        return this.guildTag;
    }

    public int getLevel() {
        return this.level;
    }

    public int getClassification() {
        return this.classification;
    }

    public int getHealth() {
        return this.health;
    }

    public int getMaxHealth() {
        return this.maxHealth;
    }

    public int getMana() {
        return this.mana;
    }

    public int getMaxMana() {
        return this.maxMana;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public int getSkin() { return this.skin; }

    public int getHairStyle() { return this.hairStyle; }

    public int getHairColor() { return this.hairColor; }

    public void walk(Direction direction, int x, int y) {
        this.direction = direction;
        this.x = x;
        this.y = y;
    }

    public void attack(Direction direction) {
        this.direction = direction;
    }

    public void face(Direction direction) {
        this.direction = direction;
    }

    @Override
    public int hashCode() {
        return this.uid;
    }

    public enum State {
        STAND,
        CHAIR,
        FLOOR;

        public static State valueOf(int id) {
            return State.values()[id % State.values().length];
        }
    }

}
