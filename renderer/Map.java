package com.endlessonline.client.renderer;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.endlessonline.client.managers.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.endlessonline.client.EOGame;
import com.endlessonline.client.world.*;
import com.endlessonline.client.world.Character;

import java.io.IOException;
import java.util.*;

public class Map {
    private EOMap map;
    private final EOGame game;
    private static final int TILE_WIDTH = 64;
    private static final int TILE_HEIGHT = 32;
    private final AssetManager manager;
    private MapCharacter mapCharacter;
    private MapNPC mapNpc;
    private TextureAtlas atlas;
    private final java.util.Map<Integer, MapCharacter> mapCharacters = new HashMap();
    private final java.util.Map<Integer, MapNPC> mapNPCs = new HashMap();
    private final java.util.Map<Integer, MapItem> mapItems = new HashMap();
    private final java.util.Map<Integer, MapAnimatedTile> mapAnimatedTiles = new HashMap();
    BitmapFont font;

    public boolean loaded;

    public Map(EOMap map, EOGame game) throws IOException {
        this.game = game;
        this.map = map;
        this.manager = game.getManager();
        this.font = new BitmapFont();
    }

    public java.util.Map<Integer, MapNPC> getMapNPCs() {
        return this.mapNPCs;
    }
    public java.util.Map<Integer, MapAnimatedTile> getMapAnimatedTiles() { return this.mapAnimatedTiles; }

    public void createMapAssets() throws IOException {
        this.map = this.game.getMap();
        this.atlas = manager.loadMapAtlas(this.map);

        for (int i=0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                if (map.getTile(i, j).getGraphic(EOMap.Layer.GROUND) != 0) {
                    if (!getMapAnimatedTiles().containsKey(map.getTile(i, j).getGraphic(EOMap.Layer.GROUND))) {
                        if (atlas.findRegion("GROUND", this.map.getTile(i, j).getGraphic(EOMap.Layer.GROUND)).packedWidth > 64) {
                            getMapAnimatedTiles().put(this.map.getTile(i, j).hasGraphic(EOMap.Layer.GROUND), new MapAnimatedTile(atlas.findRegion("GROUND", this.map.getTile(i, j).hasGraphic(EOMap.Layer.GROUND))));
                        }
                    }
                }
            }
        }

        java.util.Map<Integer, NPC> npcs = this.game.getWorld().getNpcs();
        for (java.util.Map.Entry<Integer, NPC> entry : npcs.entrySet()) {
            mapNpc = new MapNPC(this.game, entry.getValue());
            mapNpc.generateAtlas();
            mapNPCs.put(entry.getValue().getUID(), mapNpc);
            mapNpc = null;
        }

        java.util.Map<Integer, Character> chars = this.game.getWorld().getChars();
        for (java.util.Map.Entry<Integer, Character> entry : chars.entrySet()) {
            mapCharacter = new MapCharacter(this.game, entry.getValue());
            mapCharacters.put(entry.getValue().getUID(), mapCharacter);
        }

        java.util.Map<Integer, Item> items = this.game.getWorld().getItems();
        for (java.util.Map.Entry<Integer, Item> entry : items.entrySet()) {
            MapItem mapItem = new MapItem(this.game, entry.getValue());
            mapItems.put(entry.getValue().getUID(), mapItem);
        }

        loaded = true;
    }


    public static float worldToScreenX(float x, float y) {
        return ((x - y) * TILE_WIDTH / 2);
    }

    public static float worldToScreenY(float x, float y) {
        return -((x + y) * TILE_HEIGHT / 2);
    }

    public static float screenToWorldX(float x, float y) {
        x /= (TILE_WIDTH / 2f);
        y /= -(TILE_HEIGHT);
        return Math.round((2 * y + x) / 2);
    }

    public static float screenToWorldY(float x, float y) {
        x /= (TILE_WIDTH / 2f);
        y /= -(TILE_HEIGHT);
        return Math.round((2 * y - x) / 2);
    }

    public void drawMap() {
        int height = map.getHeight();
        int width = map.getWidth();

        int uid = game.getWorld().getPlayer().getUID();
                
        // Ground
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {
                        if (map.getTile(i, j).hasGraphic(EOMap.Layer.GROUND) == null) {
                            game.batch.draw(atlas.findRegion("FILL"), worldToScreenX(i, j), worldToScreenY(i, j));
                        }
                    }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.GROUND) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.GROUND));
                            if (atlas.findRegion("GROUND", id).packedWidth > 69) {
                                getMapAnimatedTiles().get(id).render(game.batch, worldToScreenX(i, j), worldToScreenY(i, j));
                            } else {
                                game.batch.draw(atlas.findRegion("GROUND", id), worldToScreenX(i, j), worldToScreenY(i, j));
                            }
                        }
                    }
                }
            }
        }

        // Shadows
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {
                        if (this.map.getTile(i, j).hasGraphic(EOMap.Layer.SHADOWS) != null && map.getTile(i, j).hasGraphic(EOMap.Layer.SHADOWS) != 0) {
                            this.game.batch.setColor(this.game.batch.getColor().r, this.game.batch.getColor().g, this.game.batch.getColor().b, 0.3f);
                            this.game.batch.draw(atlas.findRegion("SHADOWS", (this.map.getTile(i, j).hasGraphic(EOMap.Layer.SHADOWS))), worldToScreenX(i, j) - 24, worldToScreenY(i, j) - ((atlas.findRegion("SHADOWS", (this.map.getTile(i, j).hasGraphic(EOMap.Layer.SHADOWS))).packedHeight-2)/3f) + 14);
                            this.game.batch.setColor(this.game.batch.getColor().r, this.game.batch.getColor().g, this.game.batch.getColor().b, 1f);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {
                        if (this.map.getTile(i, j).hasItem()) {
                            this.mapItems.get(this.map.getTile(i, j).getItems().iterator().next().getUID()).drawItem(this.game.batch, worldToScreenX(i, j) + 10, worldToScreenY(i, j) - 4);
                        }
                    }
                }
            }
        }

        // Loop for Objects, Walls, Entities, Overlay_1, Top, Roof
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {

                        //Objects
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.OBJECTS) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.OBJECTS));
                            game.batch.draw(atlas.findRegion("OBJECTS", id), (worldToScreenX(i, j) + ((64 - atlas.findRegion("OBJECTS", id).packedWidth - 3) / 2)), (worldToScreenY(i, j)) + 2);
                        }

                        //Down Walls
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.DOWN_WALLS) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.DOWN_WALLS));
                            game.batch.draw(atlas.findRegion("DOWN_WALLS", id), worldToScreenX(i, j), worldToScreenY(i, j) + 1);
                        }

                        //Right Walls
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.RIGHT_WALLS) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.RIGHT_WALLS));
                            game.batch.draw(atlas.findRegion("RIGHT_WALLS", id), worldToScreenX(i, j) + 32, worldToScreenY(i, j) + 1);
                        }

                        // ENTITIES
                        if (this.map.getTile(i, j).hasNpc()) {
                            this.mapNPCs.get(this.map.getTile(i, j).getNpcs().iterator().next().getUID()).renderNpc(this.game.batch, worldToScreenX(i, j) + (64 / 2) - (this.mapNPCs.get(this.map.getTile(i, j).getNpcs().iterator().next().getUID()).getWidth() / 2), worldToScreenY(i, j) + 8);
                        }

                        if (this.map.getTile(i, j).hasChar()) {
                            int id = this.map.getTile(i, j).getFirstChar().getUID();
                            if (id != uid) {
                                if (mapCharacters.containsKey(id)) {
                                    this.mapCharacters.get(id).renderCharacter(this.game.batch, worldToScreenX(this.mapCharacters.get(id).mapX, this.mapCharacters.get(id).mapY) - 31, worldToScreenY(this.mapCharacters.get(id).mapX, this.mapCharacters.get(id).mapY));
                                }
                            }
                        }

                        //
                        // this.mapCharacters.get(uid).mapY;
                        if (game.getWorld().getChars().get(uid).getX() == i && game.getWorld().getChars().get(uid).getY() == j) {
                            this.mapCharacters.get(uid).renderCharacter(this.game.batch, worldToScreenX(game.getWorld().getChars().get(uid).getX(), game.getWorld().getChars().get(uid).getY()) - 31, worldToScreenY(game.getWorld().getChars().get(uid).getX(), game.getWorld().getChars().get(uid).getY()));
                        }

                        //Overlay_1
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.OVERLAYS_1) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.OVERLAYS_1));
                            game.batch.draw(atlas.findRegion("OVERLAYS_1", id), (worldToScreenX(i, j) + ((64 - atlas.findRegion("OVERLAYS_1", id).packedWidth - 3) / 2f)), worldToScreenY(i, j) + 2);
                        }

                        //Tops
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.TOPS) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.TOPS));
                            game.batch.draw(atlas.findRegion("TOPS", id), worldToScreenX(i, j) - 32, worldToScreenY(i, j) + 32);
                        }

                        //Roof
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.ROOFS) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.ROOFS));
                            game.batch.draw(atlas.findRegion("ROOFS", id), worldToScreenX(i, j) - 32, worldToScreenY(i, j) + 64);
                        }
                    }
                }
            }
        }

        //Overlay 2
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i+16 >= game.getWorld().getChars().get(uid).getX() && i-16 <= game.getWorld().getChars().get(uid).getX()) {
                    if (j+16 >= game.getWorld().getChars().get(uid).getY() && j-16 <= game.getWorld().getChars().get(uid).getY()) {
                        if (map.getTile(i, j).getGraphic(EOMap.Layer.OVERLAYS_2) != 0) {
                            int id = (this.map.getTile(i, j).hasGraphic(EOMap.Layer.OVERLAYS_2));
                            game.batch.draw(atlas.findRegion("OVERLAYS_2", id), worldToScreenX(i, j), worldToScreenY(i, j)+64);
                        }
                    }
                }
            }
        }

        //Character transparency behind layers
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (this.map.getTile(i, j).hasChar()) {

                }
            }
        }
    }

    public java.util.Map<Integer, MapCharacter> getMapCharacters() {
        return this.mapCharacters;
    }

    public void walkPlayer(int uid, Direction direction, int x, int y) {
        int fromx = x;
        int fromy = y;
        if (direction == Direction.DOWN) {
            fromy--;
        }
        if (direction == Direction.LEFT) {
            fromx++;
        }
        if (direction == Direction.UP) {
            fromy++;
        }
        if (direction == Direction.RIGHT) {
            fromx--;
        }
        if (mapCharacters.get(uid) != null) {
            mapCharacters.get(uid).mapX = fromx;
            mapCharacters.get(uid).mapY = fromy;
            mapCharacters.get(uid).walkPlayer(direction, uid, x, y);
            mapCharacters.get(uid).setState(MapCharacter.renderState.WALKING);
        }
    }

    public void update() {

    }

    public void dispose() {
        atlas.dispose();
        for(java.util.Map.Entry<Integer, MapCharacter> entry : mapCharacters.entrySet()) {
            entry.getValue().dispose();
        }
        for(java.util.Map.Entry<Integer, MapNPC> entry : mapNPCs.entrySet()) {
            entry.getValue().dispose();
        }
        for(java.util.Map.Entry<Integer, MapItem> entry : mapItems.entrySet()) {
            entry.getValue().dispose();
        }
    }

    public void disposeAndCreate() {
        atlas.dispose();
        for(java.util.Map.Entry<Integer, MapCharacter> entry : mapCharacters.entrySet()) {
            entry.getValue().dispose();
        }
        for(java.util.Map.Entry<Integer, MapNPC> entry : mapNPCs.entrySet()) {
            entry.getValue().dispose();
        }
        for(java.util.Map.Entry<Integer, MapItem> entry : mapItems.entrySet()) {
            entry.getValue().dispose();
        }
        this.map = this.game.getMap();
    }
}
