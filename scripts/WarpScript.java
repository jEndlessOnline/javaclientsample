package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.warp.WarpMapEvent;

import java.io.IOException;

public class WarpScript implements Script {
    private EOEngine engine;

    @Override
    public void onStart(EOEngine engine) {
        this.engine = engine;
        this.engine.register(WarpMapEvent.class, this::onWarpMap);
    }

    private void onWarpMap(WarpMapEvent event) {
        // blah go fuck yourself,
        // figure out how to render the new map to the screen without breaking shit
    }

    @Override
    public void update(long delta) {

    }

    @Override
    public void onEnd() {

    }
}
