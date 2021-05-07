package com.endlessonline.client.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class MapAnimatedTile {

    private TextureRegion tile;
    private TextureRegion[][] splitTiles;
    private Animation<TextureRegion> animation;
    private TextureRegion keyFrame;

    private float stateTime;

    public MapAnimatedTile(TextureRegion tile) {
        this.tile = tile;
        this.create();
    }

    public void create() {
        this.splitTiles = tile.split(64, tile.getRegionHeight());
        TextureRegion[] frames = new TextureRegion[4];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 4; j++) {
                frames[index++] = splitTiles[i][j];
            }
        }
        animation = new Animation(8f, frames);
        this.keyFrame = frames[0];

    }

    public void render(SpriteBatch batch, float x, float y) {
        this.stateTime += Gdx.graphics.getDeltaTime();
        keyFrame = animation.getKeyFrame(stateTime, true);
        batch.draw(keyFrame, x, y);
    }

    public void update(float delta) {

    }

    public void dispose() {
        // ???? idk
        // what can even be disposed here
        // the textures are disposed elsewhere so?????
    }
}
