package com.endlessonline.client.io;


public class ENFPub extends Pub<ENF> {

    @Override
    protected void serializeEntry(EOWriter writer, Entry<ENF> entry) { }

    @Override
    protected Entry<ENF> unserializeEntry(EOReader reader) {
        Entry<ENF> entry = new Entry<>();
        entry.setName(reader.readFixedString(reader.readChar()));
        entry.setProperty(ENF.GFX_ID, reader.readShort());
        reader.skip(1);

        entry.setProperty(ENF.BOSS, reader.readShort());
        entry.setProperty(ENF.CHILD, reader.readShort());
        entry.setProperty(ENF.TYPE, reader.readShort());
        entry.setProperty(ENF.VENDOR, reader.readShort());
        entry.setProperty(ENF.HEALTH, reader.readThree());
        reader.skip(2);

        entry.setProperty(ENF.MINIMUM_DAMAGE, reader.readShort());
        entry.setProperty(ENF.MAXIMUM_DAMAGE, reader.readShort());
        entry.setProperty(ENF.ACCURACY, reader.readShort());
        entry.setProperty(ENF.EVASION, reader.readShort());
        entry.setProperty(ENF.DEFENSE, reader.readShort());
        reader.skip(10);

        entry.setProperty(ENF.EXPERIENCE, reader.readShort());
        reader.skip(1);
        return entry;
    }
}
