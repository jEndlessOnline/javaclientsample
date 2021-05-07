package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;

public interface Script {
    void onStart(EOEngine engine);
    void update(long delta);
    void onEnd();
}
