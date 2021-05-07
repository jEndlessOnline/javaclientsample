package com.endlessonline.client.io;

public class EIFPub extends Pub<EIF> {
    @Override
    protected void serializeEntry(EOWriter writer, Entry<EIF> entry) { }

    @Override
    protected Entry<EIF> unserializeEntry(EOReader reader) {
        Entry<EIF> entry = new Entry<>();
        entry.setName(reader.readFixedString(reader.readChar()));
        entry.setProperty(EIF.ICON, reader.readShort());
        entry.setProperty(EIF.TYPE, reader.readChar());
        entry.setProperty(EIF.SUB_TYPE, reader.readChar());
        entry.setProperty(EIF.QUALITY, reader.readChar());
        entry.setProperty(EIF.HEALTH, reader.readShort());
        entry.setProperty(EIF.MANA, reader.readShort());
        entry.setProperty(EIF.MINIMUM_DAMAGE, reader.readShort());
        entry.setProperty(EIF.MAXIMUM_DAMAGE, reader.readShort());
        entry.setProperty(EIF.ACCURACY, reader.readShort());
        entry.setProperty(EIF.EVASION, reader.readShort());
        entry.setProperty(EIF.DEFENSE, reader.readShort());
        reader.skip(1);

        entry.setProperty(EIF.STRENGTH, reader.readChar());
        entry.setProperty(EIF.INTELLIGENCE, reader.readChar());
        entry.setProperty(EIF.WISDOM, reader.readChar());
        entry.setProperty(EIF.AGILITY, reader.readChar());
        entry.setProperty(EIF.CONSTITUTION, reader.readChar());
        entry.setProperty(EIF.CHARISMA, reader.readChar());
        reader.skip(6);

        int special1 = reader.readThree();
        entry.setProperty(EIF.SCROLL_MAP, special1);
        entry.setProperty(EIF.GFX_ID, special1);
        entry.setProperty(EIF.EXP_REWARD, special1);
        entry.setProperty(EIF.HAIR_COLOR, special1);
        entry.setProperty(EIF.EFFECT, special1);
        entry.setProperty(EIF.KEY, special1);

        int special2 = reader.readChar();
        entry.setProperty(EIF.GENDER, special2);
        entry.setProperty(EIF.SCROLL_X, special2);

        int special3 = reader.readChar();
        entry.setProperty(EIF.SCROLL_Y, special3);

        entry.setProperty(EIF.LEVEL_REQUIRED, reader.readShort());
        entry.setProperty(EIF.CLASS_REQUIRED, reader.readShort());
        entry.setProperty(EIF.STRENGTH_REQUIRED, reader.readShort());
        entry.setProperty(EIF.INTELLIGENCE_REQUIRED, reader.readShort());
        entry.setProperty(EIF.WISDOM_REQUIRED, reader.readShort());
        entry.setProperty(EIF.AGILITY_REQUIRED, reader.readShort());
        entry.setProperty(EIF.CONSTITUTION_REQUIRED, reader.readShort());
        entry.setProperty(EIF.CHARISMA, reader.readShort());
        reader.skip(2);

        entry.setProperty(EIF.WEIGHT, reader.readChar());
        reader.skip(1);

        entry.setProperty(EIF.SIZE, reader.readChar());



        return entry;
    }
}
