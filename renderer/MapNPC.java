package com.endlessonline.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.endlessonline.client.EOEngine;
import com.endlessonline.client.io.ENF;
import com.endlessonline.client.io.PEFile;
import com.endlessonline.client.managers.AssetManager;
import com.endlessonline.client.world.Direction;
import com.endlessonline.client.world.NPC;

import java.io.IOException;

public class MapNPC {

    EOEngine engine;
    private AssetManager manager;

    private NPC npc;
    private int id;
    private Direction direction;
    private int x;
    private int y;
    private int gfxId;
    private int width;
    private int height;
    private int xOff;
    private int yOff;
    private int counter = 0;
    private long timer = 0;
    private long animTime = 80;

    public int destx, desty;

    public boolean isWalking = false;
    /*

    int id = 1;
    int multiplier = id-1;
    int gfxId = 101 + (40*multiplier);

     */

    private TextureAtlas frames;
    private TextureRegion[] walking;
    private TextureRegion keyFrame;

    public MapNPC(EOEngine engine, NPC npc) {
        this.engine = engine;
        this.manager = this.engine.getManager();
        this.npc = npc;
        this.id = this.npc.getUID();
        this.x = this.npc.getX();
        this.y = this.npc.getY();
        this.direction = this.npc.getDirection();
        this.gfxId = this.engine.getENF().getEntry(this.npc.getID()).getProperty(ENF.GFX_ID);
    }

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public Pixmap flipPixmap(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel(width - x - 1, y));
            }
        }

        return flipped;
    }

    public void generateAtlas() throws IOException {
        if (!this.manager.getMapNpcAtlas().containsKey(this.gfxId)) {
            PEFile file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx021.egf").file());

            PixmapPacker packer = new PixmapPacker(2048, 2048, Pixmap.Format.RGB565, 2, false);
            Pixmap pixmap;
            int resId = (101 + (40*(this.gfxId-1)));

            for (int i = 0; i < 16; i++) {
                pixmap = file.getResourceByIndex(resId+i);
                packer.pack("frame"+(i + 1), pixmap);
                pixmap = flipPixmap(pixmap);
                packer.pack("flippedFrame"+(i+1), pixmap);
                pixmap.dispose();
            }

            TextureAtlas finalAtlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
            this.frames = finalAtlas;
            this.manager.getMapNpcAtlas().put(this.gfxId, finalAtlas);
            this.width = this.frames.getRegions().get(0).packedWidth;
            this.height = this.frames.getRegions().get(0).packedHeight;
            packer.dispose();
            if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.DOWN) {
                keyFrame = this.frames.findRegion("frame1");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.LEFT) {
                keyFrame = this.frames.findRegion("frame3");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.UP) {
                keyFrame = this.frames.findRegion("flippedFrame3");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.RIGHT) {
                keyFrame = this.frames.findRegion("flippedFrame1");
            } else {
                keyFrame = this.frames.findRegion("flippedFrame1");
            }
        } else {
            this.frames = this.manager.getMapNpcAtlas().get(this.gfxId);
            this.width = this.frames.getRegions().get(0).packedWidth;
            this.height = this.frames.getRegions().get(0).packedHeight;
            if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.DOWN) {
                keyFrame = this.frames.findRegion("frame1");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.LEFT) {
                keyFrame = this.frames.findRegion("frame3");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.UP) {
                keyFrame = this.frames.findRegion("flippedFrame3");
            } else if (this.engine.getWorld().getNpcs().get(npc.getUID()).getDirection() == Direction.RIGHT) {
                keyFrame = this.frames.findRegion("flippedFrame1");
            } else {
                keyFrame = this.frames.findRegion("flippedFrame1");
            }
        }

    }

    public void renderNpc(SpriteBatch batch, float x, float y) {
            batch.draw(this.keyFrame, x + xOff, y + yOff);
    }

    public void walkNpc(Direction direction, int counter, int uid, int x, int y) {

    }

    public void faceNpc(Direction direction) {
        this.direction = direction;
    }

    public void update(float delta) {
        this.timer += delta;
        if (isWalking) {
            if (timer >= animTime) {
                if (direction == Direction.DOWN) {
                    if (counter > 3) {
                        this.keyFrame = this.frames.findRegion("frame1");
                        xOff = 0;
                        yOff = 0;
                        this.engine.getWorld().walkNPC(this.npc.getUID(), direction, x, y + 1);
                        this.x = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getX();
                        this.y = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getY();
                        this.timer = 0;
                        this.counter = 0;
                        this.isWalking = false;
                    } else {
                        keyFrame = this.frames.findRegion("frame" + (5 + counter));
                        xOff -= 8;
                        yOff -= 4;
                        this.counter++;
                        this.timer = 0;
                    }
                } else if (direction == Direction.LEFT) {
                    if (counter > 3) {
                        this.keyFrame = this.frames.findRegion("frame3");
                        xOff = 0;
                        yOff = 0;
                        this.engine.getWorld().walkNPC(this.npc.getUID(), direction, x - 1, y);
                        this.x = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getX();
                        this.y = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getY();
                        this.timer = 0;
                        this.counter = 0;
                        this.isWalking = false;
                    } else {
                        keyFrame = this.frames.findRegion("frame" + (9 + counter));
                        xOff -= 8;
                        yOff += 4;
                        this.counter++;
                        this.timer = 0;
                    }
                } else if (direction == Direction.UP) {
                    if (counter > 3) {
                        this.keyFrame = this.frames.findRegion("flippedFrame3");
                        xOff = 0;
                        yOff = 0;
                        this.engine.getWorld().walkNPC(this.npc.getUID(), direction, x, y - 1);
                        this.x = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getX();
                        this.y = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getY();
                        this.timer = 0;
                        this.counter = 0;
                        this.isWalking = false;
                    } else {
                        keyFrame = this.frames.findRegion("flippedFrame" + (9 + counter));
                        xOff += 8;
                        yOff += 4;
                        this.counter++;
                        this.timer = 0;
                    }
                } else if (direction == Direction.RIGHT) {
                    if (counter > 3) {
                        this.keyFrame = this.frames.findRegion("flippedFrame1");
                        xOff = 0;
                        yOff = 0;
                        this.engine.getWorld().walkNPC(this.npc.getUID(), Direction.RIGHT, this.x + 1, this.y);
                        this.x = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getX();
                        this.y = this.engine.getWorld().getNpcs().get(this.npc.getUID()).getY();
                        this.timer = 0;
                        this.counter = 0;
                        this.isWalking = false;
                    } else {
                        keyFrame = this.frames.findRegion("flippedFrame" + (5 + counter));
                        xOff += 8;
                        yOff -= 4;
                        this.counter++;
                        this.timer = 0;
                    }
                }
            }
        } else {
            if (direction == Direction.DOWN) {
                this.keyFrame = this.frames.findRegion("frame1");
            } else if (direction == Direction.LEFT) {
                this.keyFrame = this.frames.findRegion("frame3");
            } else if (direction == Direction.UP) {
                this.keyFrame = this.frames.findRegion("flippedFrame3");
            } else if (direction == Direction.RIGHT) {
                this.keyFrame = this.frames.findRegion("flippedFrame1");
            }
        }

    }

    public void dispose() {
        this.frames.dispose();
    }
}
