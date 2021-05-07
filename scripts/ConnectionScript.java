package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.connection.ConnectionFailedEvent;
import com.endlessonline.client.events.connection.ConnectionLostEvent;
import com.endlessonline.client.events.connection.ConnectionSuccessEvent;
import com.endlessonline.client.events.init.InitBannedEvent;
import com.endlessonline.client.events.init.InitOutdatedEvent;
import com.endlessonline.client.events.map.MapRefreshEvent;
import com.endlessonline.client.io.PacketFactory;
import com.endlessonline.client.logging.Logger;
import com.endlessonline.client.renderer.MapCharacter;
import com.endlessonline.client.renderer.MapNPC;

import java.io.IOException;

public class ConnectionScript implements Script {

    private EOEngine engine;

    @Override
    public void onStart(com.endlessonline.client.EOEngine engine) {
        this.engine = engine;
        this.engine.register(ConnectionSuccessEvent.class, this::onConnectionSuccess);
        this.engine.register(ConnectionFailedEvent.class, this::onConnectionFailed);
        this.engine.register(ConnectionLostEvent.class, this::onConnectionLost);
        this.engine.register(InitBannedEvent.class, this::onInitBanned);
        this.engine.register(InitOutdatedEvent.class, this::onInitOutdated);
        this.engine.register(MapRefreshEvent.class, this::onRefreshEvent);
        this.engine.connect(this.engine.getConfig().getHost(), this.engine.getConfig().getPort());
    }

    private void onConnectionSuccess(ConnectionSuccessEvent event) {
        this.engine.send(PacketFactory.init(this.engine.getConfig().getVersion()));
    }

    private void onConnectionFailed(ConnectionFailedEvent event) {
        // attempt reconnects ?
    }

    private void onConnectionLost(ConnectionLostEvent event) {
        // redirect to main menu
    }

    private void onInitBanned(InitBannedEvent event) {
        this.engine.log(Logger.Type.ERROR, "You are banned from this server.");
        this.engine.stop();
    }

    private void onInitOutdated(InitOutdatedEvent event) {
        this.engine.log(Logger.Type.ERROR, String.format("Invalid Version - Expected %d.%d.%d", event.getBuild(), event.getMajor(), event.getMinor()));
        this.engine.stop();
    }

    private void onRefreshEvent(MapRefreshEvent event) {
        this.engine.getWorld().getChars().clear();
        this.engine.getWorld().getNpcs().clear();
        this.engine.getWorld().getItems().clear();
        event.getCharacters().forEach(character -> {
            this.engine.getWorld().addCharacter(character);
            try {
                this.engine.getRenderer().getMapCharacters().put(character.getUID(), new MapCharacter(this.engine, character));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    @Override
    public void update(long delta) {

    }

    @Override
    public void onEnd() {
        this.engine.unregister(ConnectionSuccessEvent.class, this::onConnectionSuccess);
        this.engine.unregister(ConnectionFailedEvent.class, this::onConnectionFailed);
        this.engine.unregister(ConnectionLostEvent.class, this::onConnectionLost);
        this.engine.unregister(InitBannedEvent.class, this::onInitBanned);
        this.engine.unregister(InitOutdatedEvent.class, this::onInitOutdated);
    }
}
