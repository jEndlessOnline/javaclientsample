package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.npc.NPCAppearEvent;
import com.endlessonline.client.events.npc.NPCAttackedEvent;
import com.endlessonline.client.events.npc.NPCBossKilledEvent;
import com.endlessonline.client.events.npc.NPCUpdateEvent;
import com.endlessonline.client.renderer.MapNPC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NpcScript implements Script {
    private EOEngine engine;
    private List<NPCUpdateEvent.Movement> movements = new ArrayList<>();
    private List<Map<Integer, NPCUpdateEvent.Movement>> moves = new ArrayList<>();

    @Override
    public void onStart(EOEngine engine) {
        this.engine = engine;
        this.engine.register(NPCAppearEvent.class, this::onNpcAppear);
        this.engine.register(NPCUpdateEvent.class, this::onNpcUpdate);
        this.engine.register(NPCAttackedEvent.class, this::onNpcAttacked);
        this.engine.register(NPCBossKilledEvent.class, this::onBossKilled);
    }

    private void onNpcAppear(NPCAppearEvent event) {
        this.engine.getWorld().addNPC(event.getNPC());
        MapNPC mapNPC = new MapNPC(this.engine, event.getNPC());
        try {
            mapNPC.generateAtlas();
        } catch(IOException e) {
            e.printStackTrace();
        }
        this.engine.getRenderer().getMapNPCs().put(event.getNPC().getUID(), mapNPC);
    }

    private void onNpcUpdate(NPCUpdateEvent event) {
        for (int i = 0; i < event.getMovements().size(); i++) {
            this.engine.getRenderer().getMapNPCs().get(event.getMovements().get(i).getUID()).faceNpc(event.getMovements().get(i).getDirection());
            this.engine.getRenderer().getMapNPCs().get(event.getMovements().get(i).getUID()).isWalking = true;
        }
    }

    private void onNpcAttacked(NPCAttackedEvent event) {
        int uid = event.getUID();
        if (event.isKilled()) {
            this.engine.getWorld().removeNPC(uid);
            this.engine.getRenderer().getMapNPCs().remove(uid);
        }
    }

    private void onBossKilled(NPCBossKilledEvent event) {

    }

    @Override
    public void update(long delta) {
        this.engine.getRenderer().getMapNPCs().forEach((k, v) -> {
            v.update(delta);
        });
    }

    @Override
    public void onEnd() {
        this.engine.unregister(NPCAppearEvent.class, this::onNpcAppear);
        this.engine.unregister(NPCUpdateEvent.class, this::onNpcUpdate);
        this.engine.unregister(NPCAttackedEvent.class, this::onNpcAttacked);
        this.engine.unregister(NPCBossKilledEvent.class, this::onBossKilled);
    }
}
