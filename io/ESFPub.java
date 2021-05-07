package com.endlessonline.client.io;

public class ESFPub extends Pub<ESF> {

    @Override
    protected void serializeEntry(EOWriter writer, Entry<ESF> entry) {}

    @Override
    protected Entry<ESF> unserializeEntry(EOReader reader) {
        int nameSize = reader.readChar();
        int flavorSize = reader.readChar();
        Entry<ESF> entry = new Entry<>();
        entry.setName(reader.readFixedString(nameSize));
        entry.setFlavorText(reader.readFixedString(flavorSize));
        entry.setProperty(ESF.ICON, reader.readShort());
        entry.setProperty(ESF.GFX_ID, reader.readShort());
        entry.setProperty(ESF.MANA_COST, reader.readShort());
        entry.setProperty(ESF.STAMINA_COST, reader.readShort());
        entry.setProperty(ESF.CAST_TIME, reader.readChar());
        reader.skip(2);

        entry.setProperty(ESF.TYPE, reader.readChar());
        reader.skip(5);

        entry.setProperty(ESF.TARGET_RESTRICT, reader.readChar());
        entry.setProperty(ESF.TARGET_TYPE, reader.readChar());
        reader.skip(4);

        entry.setProperty(ESF.MINIMUM_DAMAGE, reader.readShort());
        entry.setProperty(ESF.MAXIMUM_DAMAGE, reader.readShort());
        entry.setProperty(ESF.ACCURACY, reader.readShort());
        reader.skip(5);

        entry.setProperty(ESF.HEALTH, reader.readShort());
        reader.skip(15);
        return entry;
    }
}