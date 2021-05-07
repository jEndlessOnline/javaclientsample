package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.*;
import com.endlessonline.client.events.init.*;
import com.endlessonline.client.events.welcome.GameEnteredEvent;
import com.endlessonline.client.events.welcome.SelectedCharacterEvent;
import com.endlessonline.client.io.EOFile;
import com.endlessonline.client.io.PacketFactory;
import com.endlessonline.client.io.Pub;
import com.endlessonline.client.logging.Logger;
import com.endlessonline.client.world.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Queue;

public class LoginScript implements Script {

    private EOEngine engine;
    private Queue<EOFile> downloads;
    private int map;

    @Override
    public void onStart(EOEngine engine) {
        this.engine = engine;
        this.engine.register(InitSuccessEvent.class, this::onInitSuccess);
        this.engine.register(LoginEvent.class, this::onLogin);
        this.engine.register(SelectedCharacterEvent.class, this::onSelectedCharacter);
        this.engine.register(GameEnteredEvent.class, this::onGameEntered);
        this.engine.register(EMFFileEvent.class, this::onEMFFile);
        this.engine.register(ECFFileEvent.class, this::onECFFile);
        this.engine.register(EIFFileEvent.class, this::onEIFFile);
        this.engine.register(ENFFileEvent.class, this::onENFFile);
        this.engine.register(ESFFileEvent.class, this::onESFFile);
        this.load(EOFile.ITEM, this.engine.getEIF());
        this.load(EOFile.NPC, this.engine.getENF());
        this.load(EOFile.SPELL, this.engine.getESF());
        this.load(EOFile.CLASS, this.engine.getECF());
    }

    private <T> void load(EOFile type, Pub<T> pub) {
        Path path = type.getFile(1).toPath();
        if (!Files.exists(path)) {
            return;
        }

        try {
            byte[] data = Files.readAllBytes(path);
            pub.load(data);
        } catch (IOException e) {
            this.engine.log(Logger.Type.ERROR, String.format("Failed to Load Pub - %s", type));
        }
    }

    private void onGameEntered(GameEnteredEvent event) {
        for (int i = 0; i < event.getCharacters().size(); i++) {
            engine.getWorld().addCharacter(event.getCharacters().get(i));
        }
        for (int i = 0; i < event.getNPCs().size(); i++) {
            System.out.println("NPC Added To World: " + event.getNPCs().get(i));
            engine.getWorld().addNPC(event.getNPCs().get(i));
        }
        for (int i = 0; i < event.getItems().size(); i++) {
            engine.getWorld().addItem(event.getItems().get(i));
        }
        engine.setShouldRender(true);
    }

    private void onInitSuccess(InitSuccessEvent event) {
        this.engine.send(PacketFactory.login(this.engine.getConfig().getUsername(), this.engine.getConfig().getPassword()));
    }

    private void onLogin(LoginEvent event) {
        switch (event.getStatus()) {
            case OK:
                for (LoginEvent.Character character : event.getCharacters()) {
                    if (character.getName().equalsIgnoreCase(this.engine.getConfig().getCharacterName())) {
                        this.engine.send(PacketFactory.selectCharacter(character.getUID()));
                        return;
                    }
                }
                this.engine.log(Logger.Type.ERROR, String.format("Invalid Character - '%s'", this.engine.getConfig().getCharacterName()));
                this.engine.stop();
                break;

            case WRONG_USER:
            case WRONG_USER_PASS:
                this.engine.log(Logger.Type.ERROR, "Invalid Credentials");
                this.engine.stop();
                break;
        }
    }

    private void onSelectedCharacter(SelectedCharacterEvent event) {
        this.downloads = new LinkedList<>();
        this.map = event.getMapID();
        if (this.engine.getMap().isOutdated(event.getMapHash(), event.getMapFileSize())) {
            this.downloads.add(EOFile.MAP);
        }

        if (this.engine.getECF().isOutdated(event.getECFHash(), event.getECFSize())) {
            this.downloads.add(EOFile.CLASS);
        }

        if (this.engine.getEIF().isOutdated(event.getEIFHash(), event.getEIFSize())) {
            this.downloads.add(EOFile.ITEM);
        }

        if (this.engine.getENF().isOutdated(event.getENFHash(), event.getENFSize())) {
            this.downloads.add(EOFile.NPC);
        }

        if (this.engine.getESF().isOutdated(event.getESFHash(), event.getENFSize())) {
            this.downloads.add(EOFile.SPELL);
        }
        this.finishDownloads();
        engine.getWorld().loadMap(engine.getMap());
        this.engine.getWorld().setPlayer(event.getPlayer());
    }

    private void finishDownloads() {
        if (!this.downloads.isEmpty()) {
            EOFile file = this.downloads.poll();
            this.engine.send(PacketFactory.requestFile(file));
        } else {
            this.engine.send(PacketFactory.enterGame(0));
        }
    }

    private void onEMFFile(EMFFileEvent event) {
        this.engine.getMap().load(event.getContent());
        this.cache(EOFile.MAP, this.map, event.getContent());
        this.finishDownloads();
    }

    private void onECFFile(ECFFileEvent event) {
        this.engine.getECF().load(event.getContent());
        this.cache(EOFile.CLASS, event.getID(), event.getContent());
        this.finishDownloads();
    }

    private void onEIFFile(EIFFileEvent event) {
        this.engine.getEIF().load(event.getContent());
        this.cache(EOFile.ITEM, event.getID(), event.getContent());
        this.finishDownloads();
    }

    private void onENFFile(ENFFileEvent event) {
        this.engine.getENF().load(event.getContent());
        this.cache(EOFile.NPC, event.getID(), event.getContent());
        this.finishDownloads();
    }

    private void onESFFile(ESFFileEvent event) {
        this.engine.getESF().load(event.getContent());
        this.cache(EOFile.SPELL, event.getID(), event.getContent());
        this.finishDownloads();
    }

    private void cache(EOFile type, int id, byte[] data) {
        try {
            File file = type.getFile(id);
            if (file.exists() || file.createNewFile()) {
                Files.write(file.toPath(), data);
            }
        } catch (IOException e) {
            this.engine.log(Logger.Type.ERROR, String.format("Failed to cache %s | %s", type.getPath(id), e));
        }
    }

    @Override
    public void update(long delta) {

    }

    @Override
    public void onEnd() {
        this.engine.unregister(InitSuccessEvent.class, this::onInitSuccess);
        this.engine.unregister(LoginEvent.class, this::onLogin);
        this.engine.unregister(SelectedCharacterEvent.class, this::onSelectedCharacter);
        this.engine.unregister(EMFFileEvent.class, this::onEMFFile);
        this.engine.unregister(ECFFileEvent.class, this::onECFFile);
        this.engine.unregister(EIFFileEvent.class, this::onEIFFile);
        this.engine.unregister(ENFFileEvent.class, this::onENFFile);
        this.engine.unregister(ESFFileEvent.class, this::onESFFile);
    }
}
