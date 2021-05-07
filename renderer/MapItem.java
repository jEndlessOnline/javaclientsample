package com.endlessonline.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.endlessonline.client.EOEngine;
import com.endlessonline.client.io.EIF;
import com.endlessonline.client.io.ENF;
import com.endlessonline.client.io.PEFile;
import com.endlessonline.client.managers.AssetManager;
import com.endlessonline.client.world.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapItem {
    private EOEngine engine;
    private TextureAtlas atlas;
    private int gfxId = 0;

    private AssetManager manager;

    public MapItem(EOEngine engine, Item item) throws IOException {
        this.engine = engine;
        this.manager = engine.getManager();
        this.gfxId = this.engine.getEIF().getEntry(item.getID()).getProperty(EIF.ICON);
        if (this.manager.groundItems == null) {
            generateItemAtlas();
            this.atlas = this.manager.groundItems;
        } else {
            this.atlas = this.manager.groundItems;
        }
    }

    public void generateItemAtlas() throws IOException {
        Map<Integer, Item> items = this.engine.getWorld().getItems();
        List<Integer> gfxIds = new ArrayList();
        PEFile file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx023.egf").file());
        PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGB565, 1, false);
        for (Map.Entry<Integer, Item> entry : items.entrySet()) {
            int gfx = this.engine.getEIF().getEntry(entry.getValue().getID()).getProperty(EIF.ICON);
            if (!gfxIds.contains(gfx)) {
                Pixmap bmp = file.getResourceByIndex(101 + ((gfx-1)*2));
                packer.pack(String.valueOf(gfx), bmp);
                bmp.dispose();
                gfxIds.add(gfx);
            }
        }
        this.engine.getManager().groundItems  = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
        packer.dispose();
    }

    public void drawItem(SpriteBatch batch, float x, float y) {
        batch.draw(atlas.findRegion(String.valueOf(gfxId)), x, y);
    }

    public void dispose() {
        atlas.dispose();
    }
}
