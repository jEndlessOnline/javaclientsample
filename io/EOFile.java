package com.endlessonline.client.io;

import com.badlogic.gdx.Gdx;

import java.io.File;

public enum EOFile {

    MAP(1, Gdx.files.internal("core/assets/maps/%05d.emf").path()),
    ITEM(2, Gdx.files.internal("core/assets/pub/dat%03d.eif").path()),
    NPC(3, Gdx.files.internal("core/assets/pub/dtn%03d.enf").path()),
    SPELL(4, Gdx.files.internal("core/assets/pub/dsl%03d.esf").path()),
    CLASS(5, Gdx.files.internal("core/assets/pub/dat%03d.ecf").path());

    private final int id;
    private final String path;

    EOFile(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public int getID() {
        return this.id;
    }

    public String getPath(int id) {
        return String.format(this.path, id);
    }

    public File getFile(int id) {
        return new File(this.getPath(id));
    }
}