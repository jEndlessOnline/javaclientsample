package com.endlessonline.client.io;

public class ECFPub extends Pub<ECF> {

    @Override
    protected void serializeEntry(EOWriter writer, Entry<ECF> entry) { }

    @Override
    protected Entry<ECF> unserializeEntry(EOReader reader) {
        Entry<ECF> entry = new Entry<>();
        entry.setName(reader.readFixedString(reader.readChar()));
        entry.setProperty(ECF.PARENT_CLASS, reader.readChar());
        entry.setProperty(ECF.STAT_TABLE, reader.readChar());
        entry.setProperty(ECF.STRENGTH, reader.readShort());
        entry.setProperty(ECF.INTELLIGENCE, reader.readShort());
        entry.setProperty(ECF.WISDOM, reader.readShort());
        entry.setProperty(ECF.AGILITY, reader.readShort());
        entry.setProperty(ECF.CONSTITUTION, reader.readShort());
        entry.setProperty(ECF.CHARISMA, reader.readShort());
        return entry;
    }
}
