package com.endlessonline.client.renderer;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.Event;
import com.endlessonline.client.events.character.CharacterWalkEvent;
import com.endlessonline.client.world.Character;
import com.endlessonline.client.world.Direction;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

public class MapCharacter {

    private EOEngine engine;
    private TextureAtlas atlas;
    private Character character;
    private int xOff, yOff;
    public int mapX;
    public int mapY;
    public Direction direction;
    public int dir;
    public Animation<TextureRegion>[] walking = new Animation[4];
    public Animation<TextureRegion> melee;
    boolean flipped = false;
    public boolean isWalking = false;
    public boolean isAttacking = false;
    public TextureRegion keyFrame;
    public Vector2 pos;

    private int counter = 0;
    private int frameCounter = 0;
    private long timer = 0;
    private long animTimer = 0;
    long animTime = 80;
    long walkTime;

    public boolean keepWalking = false;

    public int destx =- 1, desty = -1;

    private renderState state;



    public MapCharacter(EOEngine engine, Character character) throws IOException {
        this.engine = engine;
        this.character = character;
        this.atlas = this.engine.getManager().atlasCharacter(this.character);
        this.mapX = character.getX();
        this.mapY = character.getY();
        this.direction = this.character.getDirection();
        TextureRegion[] walkDown = new TextureRegion[4];
        TextureRegion[] walkLeft = new TextureRegion[4];
        TextureRegion[] walkUp = new TextureRegion[4];
        TextureRegion[] walkRight = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            walkDown[i] = atlas.findRegion("walkingdown"+i);
            walkLeft[i] = atlas.findRegion("walkingleft"+i);
            walkRight[i] = atlas.findRegion("walkingright"+i);
            walkUp[i] = atlas.findRegion("walkingup"+i);
        }

        walking[0] = new Animation<TextureRegion>(0.14f, walkDown);
        walking[1] = new Animation<TextureRegion>(0.14f, walkLeft);
        walking[2] = new Animation<TextureRegion>(0.14f, walkUp);
        walking[3] = new Animation<TextureRegion>(0.14f, walkRight);

        facePlayer(this.character.getDirection());
        this.state = renderState.IDLE;
    }

    public void rerender() throws IOException {
        this.atlas = this.engine.getManager().atlasCharacter(this.character);
    }

    public void renderCharacter(SpriteBatch batch, float x, float y) {
        batch.draw(keyFrame, x +(float) this.xOff, y +(float) this.yOff);
    }

    public void facePlayer(Direction direction) {
        this.direction = direction;
        if (direction == Direction.DOWN) {
            keyFrame = atlas.findRegion("frontidle");
        } else if (direction == Direction.LEFT) {
            keyFrame = atlas.findRegion("backidle");
        } else if (direction == Direction.UP) {
            keyFrame = atlas.findRegion("upidle");
        } else if (direction == Direction.RIGHT) {
            keyFrame = atlas.findRegion("rightidle");
        }
    }

    public void awalkPlayer(Direction direction, int counter, int uid, int x, int y) {

    }

    enum renderState {
        WALKING,
        IDLE
    }

    public void setState(renderState state) {

        this.state = state;
    }

    public void walkPlayer(Direction direction, int uid, int x, int y) {
        this.destx = x;
        this.desty = y;
        if (timer >= animTime) {
            if (this.direction == Direction.DOWN) {
                if (this.counter >= 4) {
                    this.engine.getWorld().walkCharacter(uid, direction, x, y);
                    xOff = 0;
                    yOff = 0;
                    this.mapY = desty;
                    this.timer = 0;
                    this.counter = 0;
                    this.isWalking = false;
                    this.destx = -1;
                    this.desty = -1;
                } else {
                    keyFrame = atlas.findRegion("walkingdown" + this.counter);
                    this.counter++;
                    this.animTimer = 0;
                    xOff -= 8;
                    yOff -= 4;
                    this.timer = 0;
                }
            } else if (this.direction == Direction.LEFT) {
                if (this.counter >= 4) {
                    this.engine.getWorld().walkCharacter(uid, direction, x, y);
                    xOff = 0;
                    yOff = 0;
                    this.mapX = destx;
                    this.timer = 0;
                    this.counter = 0;
                    this.isWalking = false;
                    this.destx = -1;
                    this.desty = -1;
                    keyFrame = atlas.findRegion("backidle");
                } else {
                    keyFrame = atlas.findRegion("walkingleft" + this.counter);
                    this.counter++;
                    this.animTimer = 0;
                    xOff -= 8;
                    yOff += 4;
                    this.timer = 0;
                }
            } else if (this.direction == Direction.UP) {
                if (this.counter >= 4) {
                    this.engine.getWorld().walkCharacter(uid, direction, x, y);
                    xOff = 0;
                    yOff = 0;
                    this.mapY = desty;
                    this.timer = 0;
                    this.counter = 0;
                    this.isWalking = false;
                    this.destx = -1;
                    this.desty = -1;
                    keyFrame = atlas.findRegion("upidle");
                } else {
                    keyFrame = atlas.findRegion("walkingup" + this.counter);
                    this.counter++;
                    this.animTimer = 0;
                    xOff += 8;
                    yOff += 4;
                    this.timer = 0;
                }
            } else if (this.direction == Direction.RIGHT) {
                if (this.counter >= 4) {
                    this.engine.getWorld().walkCharacter(uid, direction, x, y);
                    xOff = 0;
                    yOff = 0;
                    this.mapX = destx;
                    this.timer = 0;
                    this.counter = 0;
                    this.isWalking = false;
                    this.destx = -1;
                    this.desty = -1;
                    keyFrame = atlas.findRegion("rightidle");
                } else {
                    keyFrame = atlas.findRegion("walkingright" + this.counter);
                    this.counter++;
                    this.animTimer = 0;
                    xOff += 8;
                    yOff -= 4;
                    this.timer = 0;
                }
            }
        }
    }

    public void attackPlayer(Direction direction) {

    }

    public void sitPlayer(Direction direction) {

    }

    public void standPlayer(Direction direction) {

    }

    public void spellPlayer(Direction direction) {

    }

    public void killPlayer(Direction direction) {

    }

    public void update(long delta) {
        this.timer += delta;
        if (destx >= 0 || desty >= 0) {
            walkPlayer(this.direction, this.character.getUID(), destx, desty);
        }
    }

    public void dispose() {
        this.atlas.dispose();
    }
}
