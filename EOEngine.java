package com.endlessonline.client;


import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.endlessonline.client.config.Config;
import com.endlessonline.client.events.Event;
import com.endlessonline.client.io.*;
import com.endlessonline.client.logging.Logger;
import com.endlessonline.client.managers.AssetManager;
import com.endlessonline.client.renderer.Map;
import com.endlessonline.client.world.EOMap;
import com.endlessonline.client.world.World;

import java.util.function.Consumer;

public interface EOEngine {

     Config getConfig();
     Pub<ECF> getECF();
     Pub<EIF> getEIF();
     Pub<ENF> getENF();
     Pub<ESF> getESF();
     EOMap getMap();
     World getWorld();
     AssetManager getManager();
     float getStateTime();
     boolean shouldRenderMap = false;

     <T extends Event> void register(Class<T> type, Consumer<T> handler);
     <T extends Event> void unregister(Class<T> type, Consumer<T> handler);
     void setShouldRender(boolean x);
     Map getRenderer();
     void log(Logger.Type type, String text);
     void connect(String host, int port);
     void send(EOWriter packet);
     void stop();
}
