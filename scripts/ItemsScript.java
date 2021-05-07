package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.item.ItemAppearEvent;
import com.endlessonline.client.events.item.ItemDisappearEvent;
import com.endlessonline.client.events.item.ItemDroppedEvent;
import com.endlessonline.client.renderer.MapItem;

import java.io.IOException;

public class ItemsScript implements Script  {

    private EOEngine engine;
    private Thread thread;

    @Override
    public void onStart(EOEngine engine) {
        this.engine = engine;
        this.engine.register(ItemAppearEvent.class, this::onItemAppear);
        this.engine.register(ItemDisappearEvent.class, this::onItemDisappear);
    }

    private void onItemAppear(ItemAppearEvent event) {
        this.engine.getWorld().addItem(event.getItem());
    }

    private void onItemDisappear(ItemDisappearEvent event) {
        this.engine.getWorld().removeItem(event.getUID());
    }


    private void onItemDropped(ItemDroppedEvent event) {

    }

    @Override
    public void update(long delta) {

    }

    @Override
    public void onEnd() {

    }
}
