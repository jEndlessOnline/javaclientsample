package com.endlessonline.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.TimeUtils;
import com.endlessonline.client.Screen.TestScreen;
import com.endlessonline.client.config.Config;
import com.endlessonline.client.config.TestConfig;
import com.endlessonline.client.events.Event;
import com.endlessonline.client.events.EventDispatcher;
import com.endlessonline.client.events.connection.ConnectionHeartbeatEvent;
import com.endlessonline.client.events.init.InitSuccessEvent;
import com.endlessonline.client.io.*;
import com.endlessonline.client.logging.Logger;
import com.endlessonline.client.logging.SystemLogger;
import com.endlessonline.client.managers.AssetManager;
import com.endlessonline.client.renderer.Map;
import com.endlessonline.client.scripts.Script;
import com.endlessonline.client.world.EOMap;
import com.endlessonline.client.world.World;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

public class EOGame extends Game implements EOEngine {

    private final Config config;
    private final Pub<ECF> ecf;
    private final Pub<EIF> eif;
    private final Pub<ENF> enf;
    private final Pub<ESF> esf;
    private final EOMap map;
    private final World world;
    private final AssetManager manager;

    private final List<Script> scripts;
    private final EventDispatcher dispatcher;
    private final Queue<Event> events;
    private final Logger logger;
    private final NetworkService network;
    private boolean running;

    public TextArea chat;

    private Map renderer;

    public SpriteBatch batch;

    public Vector3 mouse3d = new Vector3();
    public float stateTime;

    // FPS
    long lastTimeCounted = 0;
    private float sinceChange;
    public float frameRate;

    public boolean shouldRenderMap = false;

    public EOGame(Script... scripts) {
        this.config = new TestConfig();
        this.ecf = new ECFPub();
        this.eif = new EIFPub();
        this.enf = new ENFPub();
        this.esf = new ESFPub();
        this.map = new EOMap();
        this.world = new World();

        this.scripts = new ArrayList<>();
        this.scripts.addAll(Arrays.asList(scripts));
        this.dispatcher = new EventDispatcher();
        this.events = new LinkedList<>();
        this.logger = new SystemLogger();
        this.network = new NetworkServiceImpl(event -> {
            synchronized (events) {
                events.add(event);
            }
        });
        this.manager = new AssetManager(this);
    }

    @Override
    public Map getRenderer() { return this.renderer; }

    public void setRenderer(Map renderer) { this.renderer = renderer; }

    @Override
    public void setShouldRender(boolean x) {
        shouldRenderMap = x;
    }

    @Override
    public float getStateTime() { return this.stateTime; }

    public AssetManager getManager() {
        return this.manager;
    }

    public void start() {
        this.dispatcher.register(InitSuccessEvent.class, this::onInitSuccess);
        this.dispatcher.register(ConnectionHeartbeatEvent.class, this::onConnectionHeartbeat);
        this.scripts.forEach(script -> script.onStart(this));
        this.running = true;
    }

    public TextArea getChat() {
        return this.chat;
    }

    private void onInitSuccess(InitSuccessEvent event) {
        this.network.init(event.getS1(), event.getS2(), event.getEncode(), event.getDecode());
    }

    private void onConnectionHeartbeat(ConnectionHeartbeatEvent event) {
        this.network.setSequence(event.getS1(), event.getS2());
    }

    public void update() throws InterruptedException {
        synchronized (this.events) {
            while (!this.events.isEmpty()) {
                Event event = this.events.poll();
                this.logger.log(event);
                this.dispatcher.fire(event);
            }
        }

        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();
        sinceChange += delta;

        if (sinceChange >= 1000) {
            sinceChange = 0;
            frameRate = Gdx.graphics.getFramesPerSecond();
        }

        this.scripts.forEach(script -> script.update(delta));
        Thread.sleep(1000/60 - 6); // using 60 from the config doesn't give me 60FPS? 60 - 6 gives me 60???? weird unless

    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public Pub<ECF> getECF() {
        return this.ecf;
    }

    @Override
    public Pub<EIF> getEIF() {
        return this.eif;
    }

    @Override
    public Pub<ENF> getENF() {
        return this.enf;
    }

    @Override
    public Pub<ESF> getESF() {
        return this.esf;
    }

    @Override
    public EOMap getMap() {
        return this.map;
    }

    @Override
    public World getWorld() { return this.world; }

    @Override
    public <T extends Event> void register(Class<T> type, Consumer<T> handler) {
        this.dispatcher.register(type, handler);
    }

    @Override
    public <T extends Event> void unregister(Class<T> type, Consumer<T> handler) {
        this.dispatcher.unregister(type, handler);
    }

    @Override
    public void log(Logger.Type type, String text) {
        this.logger.log(type, text);
    }

    @Override
    public void connect(String host, int port) {
        this.network.connect(host, port);
    }

    @Override
    public void send(EOWriter packet) {
        this.network.send(packet);
    }

    @Override
    public void stop() {
        this.dispatcher.unregister(InitSuccessEvent.class, this::onInitSuccess);
        this.dispatcher.unregister(ConnectionHeartbeatEvent.class, this::onConnectionHeartbeat);
        this.scripts.forEach(Script::onEnd);
        try {
            this.network.close();
        } catch (IOException e) {
            this.logger.log(Logger.Type.ERROR, e.toString());
        } finally {
            this.running = false;
        }
    }

    @Override
    public void create() {
        this.start();
        this.batch = new SpriteBatch();
        try {
            this.setScreen(new TestScreen(this));
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastTimeCounted = TimeUtils.millis();
        sinceChange = 0;
        stateTime = 0;
        frameRate = Gdx.graphics.getFramesPerSecond();
    }

    @Override
    public void render() {
        stateTime += Gdx.graphics.getDeltaTime();
        try {
            this.update();
            super.render();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose () {
        batch.dispose();
        this.stop();
        super.dispose();
    }
}