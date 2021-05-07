package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.talk.TalkLocalEvent;
import com.endlessonline.client.io.PacketFactory;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class EchoScript implements Script {

    private com.endlessonline.client.EOEngine engine;

    @Override
    public void onStart(com.endlessonline.client.EOEngine engine) {
        this.engine = engine;
        this.engine.register(TalkLocalEvent.class, this::onTalkLocal);
    }

    private void onTalkLocal(TalkLocalEvent event) {
        //this.engine.send(PacketFactory.talkLocal(event.getMessage()));
    }


    @Override
    public void update(long delta) {

    }

    @Override
    public void onEnd() {
        this.engine.unregister(TalkLocalEvent.class, this::onTalkLocal);
    }
}
