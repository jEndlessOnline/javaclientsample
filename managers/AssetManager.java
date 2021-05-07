package com.endlessonline.client.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.endlessonline.client.EOEngine;
import com.endlessonline.client.io.EIF;
import com.endlessonline.client.io.ENFPub;
import com.endlessonline.client.io.PEFile;
import com.endlessonline.client.io.Pub;
import com.endlessonline.client.world.Character;
import com.endlessonline.client.world.EOMap;
import com.endlessonline.client.world.Gender;
import com.endlessonline.client.world.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetManager {

    // use libgdx asset manager you dummy

    private Map<EOMap.Layer, HashMap<Integer, Texture>> mapTextures = new HashMap();
    private Map<Integer, Texture> npcTextures = new HashMap();
    private Map<Integer, Texture> preRendered = new HashMap();
    private Map<Integer, Texture> mainMenuTextures = new HashMap();
    private PEFile file;

    private Map<Integer, TextureAtlas> mapNpcAtlas = new HashMap();

    public Map<Integer, TextureAtlas> getMapNpcAtlas() {
        return this.mapNpcAtlas;
    }

    private EOEngine engine;

    public Map<EOMap.Layer, HashMap<Integer, Texture>> getMapAssets() { return this.mapTextures; }
    public Map<Integer, Texture> getNpcTextures() { return this.npcTextures; }
    public Map<Integer, Texture> getPreRendered() { return this.preRendered; }
    public Map<Integer, Texture> getMainMenuTextures() { return this.mainMenuTextures;}
    public TextureAtlas groundItems = null;

    PixmapPacker packer;

    public AssetManager(EOEngine engine) {
        this.engine = engine;
    }

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

    public void addPixmap(Pixmap src, Pixmap dest, int xOff, int yOff) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (src.getPixel(x, y) != Color.rgba8888(0, 0, 0, 1)) {
                    dest.drawPixel(x+xOff, y+yOff, src.getPixel(x, y));
                }
            }
        }
    }

    public void clipPixmap(Pixmap src, Pixmap dest, int xOff, int yOff) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (src.getPixel(x, y) != 134217983) {
                    dest.drawPixel(x+xOff, y+yOff, src.getPixel(x, y));
                }
            }
        }
    }

    public TextureAtlas loadMapAtlas(EOMap map) throws IOException {
        PEFile file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx003.egf").file());
        PixmapPacker packer = new PixmapPacker(1024, 1024, Pixmap.Format.RGB565, 0, false);
        Pixmap pixmap = file.getResourceByIndex(map.getFill() + 100);
        packer.pack("FILL", pixmap);
        pixmap.dispose();

        List<Integer> gfxIds = new ArrayList();

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.GROUND);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("GROUND_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.TOPS);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("TOPS_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();
        file.close();
        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx004.egf").file());

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.OBJECTS);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("OBJECTS_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();
        file.close();
        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx006.egf").file());

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.DOWN_WALLS);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("DOWN_WALLS_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.RIGHT_WALLS);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("RIGHT_WALLS_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();
        file.close();
        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx022.egf").file());

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.SHADOWS);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("SHADOWS_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();
        file.close();
        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx005.egf").file());

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.OVERLAYS_1);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("OVERLAYS_1_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();

        for (int i = 0; i < map.getWidth(); i++) {
            for (int j = 0; j < map.getHeight(); j++) {
                Integer id = map.getTile(i, j).hasGraphic(EOMap.Layer.OVERLAYS_2);
                if (id != null && id != 0) {
                    if (!gfxIds.contains(id)) {
                        pixmap = file.getResourceByIndex(id + 100);
                        packer.pack("OVERLAYS_2_"+id, pixmap);
                        gfxIds.add(id);
                        pixmap.dispose();
                    }
                }
            }
        }

        gfxIds.clear();
        file.close();

        TextureAtlas atlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
        packer.dispose();
        return atlas;
    }


    public TextureAtlas atlasCharacter(Character character) throws IOException {
        // i mean this "works"
        // but it needs to be picked apart and fixed lol.
        // also use libgdx asset manager RETARD

        packer = new PixmapPacker(2048, 2048, Pixmap.Format.RGBA8888, 0, false);
        int id = 0;

        int genderOffX = 0;
        int genderOffY = 0;

        Pixmap pixmap;
        int weapon = character.getWeapon();
        int color = character.getHairColor() * 4;
        int hair = character.getHairStyle();
        int skin = character.getSkin();
        int boots = character.getBoots();
        int gender = character.getGenderVal();
        int armor = character.getArmor();
        int shield = character.getShield();
        int hat = character.getHat();

        if (gender == 0) {
            genderOffY = 2;
        }
        Pixmap _pixmap = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        boolean backitem = false;
        List<Integer> shieldIds = new ArrayList();

        shieldIds.add(18);
        shieldIds.add(15);
        shieldIds.add(19);
        shieldIds.add(16);
        shieldIds.add(14);
        shieldIds.add(11);
        shieldIds.add(10);

        if (shieldIds.contains(shield)) {
            backitem = true;
        }

        if (backitem) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (20 - gender) + ".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getShield()-1)*50));
            addPixmap(pixmap, _pixmap, 54-16, 58-20+genderOffY);
            pixmap.dispose();
            file.close();
        }

        if (hair > 0) {
            if (gender > 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());

            } else {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
            }
            pixmap = file.getResourceByIndex(101 + ((character.getHairStyle() - 1)*40) + (color));
            addPixmap(pixmap, _pixmap, 54-6, 58-13+genderOffY);
            pixmap.dispose();
            file.close();
        }

        if (weapon != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (18 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getWeapon()-1)*100));
            addPixmap(pixmap, _pixmap, 54 - 34, 38+genderOffY);
            file.close();
            pixmap.dispose();
        }

        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx008.egf").file());

        pixmap = file.getResourceByIndex(101);
        Pixmap characterm = new Pixmap(18, 58, Pixmap.Format.RGBA8888);
        if (gender == 1) {
            characterm.drawPixmap(pixmap, 0, 0, 18*2, (skin * 58), 18, 58);
        } else {
            characterm.drawPixmap(pixmap, 0, 0, 0, (skin * 58), 18, 58);
        }
        addPixmap(characterm, _pixmap, 54, 58);
        file.close();
        pixmap.dispose();


        if (boots != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (12 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getBoots()-1)*40));
            addPixmap(pixmap, _pixmap, 54 - 8, 58 + 36);
            file.close();
            pixmap.dispose();
        }

        if (armor != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (14 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getArmor()-1)*50));
            addPixmap(pixmap, _pixmap, 54 - 8, 58 - 13);
            file.close();
            pixmap.dispose();
        }

        if (hair != 0) {
            if (gender > 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());
            } else {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
            }
            Pixmap hairm = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
            pixmap = file.getResourceByIndex(101 + ((character.getHairStyle() - 1)*40) + (color)+1);
            addPixmap(pixmap, hairm, 54-6, 58-13);
            file.close();

            if (hat != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (15 + gender) +".egf").file());
                pixmap = file.getResourceByIndex(101 + ((hat-1) * 10));
                clipPixmap(pixmap, hairm, 54-6, 58-17+(genderOffY));
                file.close();
                addPixmap(hairm, _pixmap, 0, 0);
            } else {
                addPixmap(hairm, _pixmap, 0, 0);
            }
            hairm.dispose();
            pixmap.dispose();
        }

        if (!backitem) {
            if (shield != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (20 - gender) + ".egf").file());
                pixmap = file.getResourceByIndex(101 + ((character.getShield()-1)*50));
                addPixmap(pixmap, _pixmap, 54 - 8, 58 + 16+genderOffY);
                pixmap.dispose();
                file.close();
            }
        }



        packer.pack("frontidle", _pixmap);
        id++;
        _pixmap = flipPixmap(_pixmap);
        packer.pack("rightidle", _pixmap);
        _pixmap.fill();

        if (hair > 0) {
            if (gender > 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());

            } else {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
            }
            pixmap = file.getResourceByIndex(101 + (((character.getHairStyle() - 1)*40) + (color))+2);
            addPixmap(pixmap, _pixmap, 54-6, 58-13);
            pixmap.dispose();
            file.close();
        }

        if (weapon != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (18 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getWeapon()-1)*100)+1);
            addPixmap(pixmap, _pixmap, 54 - 34, 38);
            file.close();
            pixmap.dispose();
        }

        file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx008.egf").file());

        pixmap = file.getResourceByIndex(101);
        Pixmap backcharacterm = new Pixmap(18, 58, Pixmap.Format.RGBA8888);
        if (gender == 1) {
            backcharacterm.drawPixmap(pixmap, 0, 0, 54, (skin * 58), 18, 58);
        } else {
            backcharacterm.drawPixmap(pixmap, 0, 0, 18, (skin * 58), 18, 58);
        }
        addPixmap(backcharacterm, _pixmap, 54, 58);
        file.close();
        pixmap.dispose();


        if (boots != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (12 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getBoots()-1)*40)+1);
            addPixmap(pixmap, _pixmap, 54 - 8, 58 + 36);
            file.close();
            pixmap.dispose();
        }

        if (armor != 0) {
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (14 - gender) +".egf").file());
            pixmap = file.getResourceByIndex(101 + ((character.getArmor()-1)*50)+1);
            addPixmap(pixmap, _pixmap, 54 - 8, 58 - 13);
            file.close();
            pixmap.dispose();
        }

        if (hair != 0) {
            if (gender > 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());
            } else {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
            }
            Pixmap hairm = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
            pixmap = file.getResourceByIndex(101 + ((character.getHairStyle() - 1)*40) + (color)+3);
            addPixmap(pixmap, hairm, 54-6, 58-13);
            file.close();

            if (hat != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (15 + gender) +".egf").file());
                pixmap = file.getResourceByIndex(101 + ((hat-1) * 10)+2);
                clipPixmap(pixmap, hairm, 54-6, 58-17);
                file.close();
                addPixmap(hairm, _pixmap, 0, 0);
            } else {
                addPixmap(hairm, _pixmap, 0, 0);
            }
            hairm.dispose();
            pixmap.dispose();
        }

        if (backitem) {
            if (shield != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (20 - gender) + ".egf").file());
                pixmap = file.getResourceByIndex(101 + ((character.getShield()-1)*50)+1);
                addPixmap(pixmap, _pixmap, 54 - 16, 58 - 20);
                pixmap.dispose();
                file.close();
            }
        }

        packer.pack("backidle", _pixmap);
        id++;
        _pixmap = flipPixmap(_pixmap);
        packer.pack("upidle", _pixmap);
        _pixmap.dispose();


        file.close();


        pixmap = file.getResourceByIndex(102);

        for (int i = 0; i < 4; i++) {
            Pixmap walking = new Pixmap(128, 128, Pixmap.Format.RGB565);

            if (backitem) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (20 - gender) + ".egf").file());
                pixmap = file.getResourceByIndex(101 + ((character.getShield()-1)*50));
                addPixmap(pixmap, walking, 54-16, 58-20+genderOffY);
                pixmap.dispose();
                file.close();
            }

            if (hair > 0) {
                if (gender > 0) {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());

                } else {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
                }
                pixmap = file.getResourceByIndex(101 + ((character.getHairStyle() - 1)*40) + (color) + i);
                addPixmap(pixmap, walking, 54-6, 58-10+genderOffY);
                pixmap.dispose();
                file.close();
            }

            if (weapon != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (18 - gender) +".egf").file());
                pixmap = file.getResourceByIndex(103 + ((character.getWeapon()-1)*100) + i);
                addPixmap(pixmap, walking, 54 - 34, 38+genderOffY);
                file.close();
                pixmap.dispose();
            }

            Pixmap walkcharacterm = new Pixmap(26, 60, Pixmap.Format.RGBA8888);
            file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx008.egf").file());
            pixmap = file.getResourceByIndex(102);
            if (gender == 1) {
                walkcharacterm.drawPixmap(pixmap, 0, 0, (26*8)+(i*26), (skin * 60), 26, 68);
            } else {
                walkcharacterm.drawPixmap(pixmap, 0, 0, i*26, (skin * 60), 26, 60);
            }
            addPixmap(walkcharacterm, walking, 54 - 5, 58 - 1);

            if (boots != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (12 - gender) +".egf").file());
                pixmap = file.getResourceByIndex(103 + ((character.getBoots()-1)*40)+i);
                addPixmap(pixmap, walking, 54 - 8, 58 + 35+genderOffY);
                file.close();
                pixmap.dispose();
            }

            if (armor != 0) {
                file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (14 - gender) +".egf").file());
                pixmap = file.getResourceByIndex(103 + ((character.getArmor()-1)*50)+i);
                addPixmap(pixmap, walking, 54 - 8, 58 - 14+genderOffY);
                file.close();
                pixmap.dispose();
            }

            if (hair != 0) {
                if (gender > 0) {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx009.egf").file());
                } else {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx010.egf").file());
                }
                Pixmap hairm = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
                pixmap = file.getResourceByIndex(101 + ((character.getHairStyle() - 1)*40) + (color)+1);
                addPixmap(pixmap, hairm, 54-6, 58-13+genderOffY);
                file.close();

                if (hat != 0) {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (15 + gender) +".egf").file());
                    pixmap = file.getResourceByIndex(101 + ((hat-1) * 10));
                    clipPixmap(pixmap, hairm, 54-6, 58-17+(genderOffY));
                    file.close();
                    addPixmap(hairm, walking, 0, 0);
                } else {
                    addPixmap(hairm, walking, 0, 0);
                }

                hairm.dispose();
                pixmap.dispose();
            }

            if (!backitem) {
                if (shield != 0) {
                    file = new PEFile(Gdx.files.internal("core/assets/gfx/gfx0" + (20 - gender) + ".egf").file());
                    pixmap = file.getResourceByIndex(103 + ((character.getShield()-1)*50)+i);
                    addPixmap(pixmap, walking, 54 - 8, 58 + 16+genderOffY);
                    pixmap.dispose();
                    file.close();
                }
            }

            packer.pack("walkingdown"+i, walking);
            walking = flipPixmap(walking);
            packer.pack("walkingright"+i, walking);
            walking.dispose();
            walking = new Pixmap(128, 128, Pixmap.Format.RGB565);
            walking.drawPixmap(pixmap, 54, 58+1, 26*(4+i), 0, 26, 60);
            packer.pack("walkingleft"+i, walking);
            walking = flipPixmap(walking);
            packer.pack("walkingup"+i, walking);
            walking.dispose();

        }

        TextureAtlas atlas = packer.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
        packer.dispose();

        return atlas;
    }

}
