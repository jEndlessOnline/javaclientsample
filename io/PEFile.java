package com.endlessonline.client.io;

import com.badlogic.gdx.graphics.Pixmap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class PEFile implements Closeable {

    private static final int BITMAP_HEADER_SIZE = 14;
    private static final int BITMAP_INFO_HEADER_SIZE = 40;

    private final RandomAccessFile file;
    private final byte[] buffer = new byte[4];
    private final ByteBuffer wrapper = ByteBuffer.wrap(this.buffer).order(ByteOrder.LITTLE_ENDIAN);
    private final ResourceDirectoryEntry bitmapDirectoryEntry = new ResourceDirectoryEntry();
    private final Map<Integer, BitmapInfo> resources = new HashMap<>();
    private int virtualAddress;
    private int rootAddress;

    public boolean isLoaded;

    public PEFile(File file) throws IOException {
        this.file = new RandomAccessFile(file, "r");
        this.readHeader();
        this.readBitmapTable();
        this.isLoaded = true;
    }

    public boolean isLoaded() { return isLoaded; }

    private int readShort() throws IOException {
        this.file.readFully(this.buffer, 0, 2);
        ((Buffer)this.wrapper).position(0);
        return this.wrapper.getShort();
    }

    private int readDirectoryEntries() throws IOException {
        this.file.skipBytes(0x0C);
        int namedEntries = this.readShort();
        int idEntries = this.readShort();
        return namedEntries + idEntries;
    }

    private int readInt() throws IOException {
        this.file.readFully(this.buffer, 0, 4);
        ((Buffer)this.wrapper).position(0);
        return this.wrapper.getInt();
    }

    private ResourceDirectoryEntry readResourceDirectoryEntry() throws IOException {
        return this.readResourceDirectoryEntry(new ResourceDirectoryEntry());
    }

    private ResourceDirectoryEntry readResourceDirectoryEntry(ResourceDirectoryEntry entry) throws IOException {
        entry.resourceType = this.readInt();
        entry.subdirectoryOffset = this.readInt();
        return entry;
    }

    private void readResourceDataEntry(ResourceDataEntry entry) throws IOException {
        entry.offset = this.readInt();
        entry.size = this.readInt();
        entry.codePage = this.readInt();
        entry.unused = this.readInt();
    }

    private void readHeader() throws IOException {
        this.file.seek(0x3C);
        int peHeaderAddress = this.readShort();

        this.file.skipBytes(peHeaderAddress - 0x3C - 0x02);
        this.file.readFully(this.buffer, 0, 4);
        String type = new String(Arrays.copyOf(this.buffer, 4));
        if (!type.equals("PE\0\0")) {
            throw new IOException();
        }

        this.file.skipBytes(0x02);
        int sections = this.readShort();

        this.file.skipBytes(0x78 - 0x04 + 0x0C);
        this.virtualAddress = this.readInt();

        this.file.skipBytes(0x6C + 0x08 + 0x04);
        for(int i=0; i<sections; i++) {
            int checkVirtualAddress = this.readInt();
            if (checkVirtualAddress == this.virtualAddress) {
                this.file.skipBytes(0x04);
                this.rootAddress = this.readInt();
                break;
            }

            this.file.skipBytes(0x24);
        }

        if (this.rootAddress == 0) {
            throw new IOException();
        }

        this.file.seek(this.rootAddress);
        int directoryEntries = this.readDirectoryEntries();
        for (int i=0; i<directoryEntries; i++) {
            this.readResourceDirectoryEntry(this.bitmapDirectoryEntry);
            if (ResourceType.valueOf(this.bitmapDirectoryEntry.resourceType) == ResourceType.BITMAP) {
                this.bitmapDirectoryEntry.subdirectoryOffset -= 0x80000000;
                return;
            }
        }

        throw new IOException("Missing bitmap resource directory");
    }

    private void readBitmapTable() throws IOException {
        this.file.seek(this.rootAddress + this.bitmapDirectoryEntry.subdirectoryOffset);
        int directoryEntries = this.readDirectoryEntries();
        List<ResourceDirectoryEntry> bitmapEntries = new ArrayList<>(directoryEntries);
        for (int i=0; i<directoryEntries; i++) {
            ResourceDirectoryEntry entry = this.readResourceDirectoryEntry();
            if (entry.subdirectoryOffset > 0x80000000) {
                entry.subdirectoryOffset -= 0x80000000;
                bitmapEntries.add(entry);
            }
        }

        ResourceDataEntry dataEntry = new ResourceDataEntry();
        for (ResourceDirectoryEntry it : bitmapEntries) {
            this.file.seek(this.rootAddress + it.subdirectoryOffset + 20);
            int entrySubdirectoryOffset = this.readInt();
            this.file.seek(this.rootAddress + entrySubdirectoryOffset);
            this.readResourceDataEntry(dataEntry);
            BitmapInfo info = new BitmapInfo();
            info.start = dataEntry.offset - this.virtualAddress + this.rootAddress;
            info.size = dataEntry.size;
            this.resources.put(it.resourceType, info);
        }
    }

    public Pixmap getResourceByIndex(int id) {
        BitmapInfo info = this.resources.get(id);
        if (info == null) {
            info = this.resources.get(103);
        }

        try {
            this.file.seek(info.start);
            byte[] data = new byte[info.size + BITMAP_HEADER_SIZE];
            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            buffer.put((byte) 'B');
            buffer.put((byte) 'M');
            buffer.putInt(data.length);
            buffer.putShort((short) 0);
            buffer.putShort((short) 0);
            buffer.putInt(BITMAP_HEADER_SIZE + BITMAP_INFO_HEADER_SIZE);
            this.file.readFully(data, BITMAP_HEADER_SIZE, info.size);
            return new Pixmap(data, 0, data.length);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() throws IOException {
        this.file.close();
    }

    private enum ResourceType {

        CURSOR(1),
        BITMAP(2),
        ICON(3),
        MENU(4),
        DIALOG(5),
        STRING_TABLE(6),
        FONT_DIRECTORY(7),
        FONT(8),
        ACCELERATOR(9),
        UNFORMATTED(10),
        MESSAGE_TABLE(11),
        GROUP_CURSOR(12),
        GROUP_ICON(14),
        VERSION_INFORMATION(16);

        private static final Map<Integer, ResourceType> TABLE = new HashMap<>(ResourceType.values().length);
        static {
            for (ResourceType rt : ResourceType.values()) {
                TABLE.put(rt.type, rt);
            }
        }

        private final int type;

        ResourceType(int type) {
            this.type = type;
        }

        public static ResourceType valueOf(int id) {
            return TABLE.get(id);
        }
    }

    private static class ResourceDirectoryEntry {

        int resourceType;
        int subdirectoryOffset;
    }

    private static class ResourceDataEntry {

        int offset;
        int size;
        int codePage;
        int unused;
    }

    private static class BitmapInfo {

        int start;
        int size;
    }
}
