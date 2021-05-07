package com.endlessonline.client.scripts;

import com.endlessonline.client.EOEngine;
import com.endlessonline.client.events.character.*;
import com.endlessonline.client.renderer.MapCharacter;
import com.endlessonline.client.world.Character;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharacterScript implements Script {

    private EOEngine engine;

    @Override
    public void onStart(EOEngine engine) {
        this.engine = engine;
        this.engine.register(CharacterAppearEvent.class, this::addCharacter);
        this.engine.register(CharacterWalkEvent.class, this::walkCharacter);
        this.engine.register(CharacterDisappearEvent.class, this::removeCharacter);
        this.engine.register(CharacterAttackEvent.class, this::onCharacterAttack);
        this.engine.register(CharacterAvatarEvent.class, this::onCharacterAvatar);
        this.engine.register(CharacterChantEvent.class, this::onCharacterChant);
        this.engine.register(CharacterEffectEvent.class, this::onCharacterEffect);
        this.engine.register(CharacterEmoteEvent.class, this::onCharacterEmote);
        this.engine.register(CharacterFaceEvent.class, this::onCharacterFace);
        this.engine.register(CharacterGroupSpellEvent.class, this::onGroupSpell);
        this.engine.register(CharacterInstrumentEvent.class, this::onCharacterInstrument);
        this.engine.register(CharacterOtherSpellEvent.class, this::onCharacterOtherSpell);
        this.engine.register(CharacterSeatEvent.class, this::onCharacterSeat);
        this.engine.register(CharacterSelfSpellEvent.class, this::onCharacterSelfSpell);
        this.engine.register(CharacterSitEvent.class, this::onCharacterSit);
        this.engine.register(CharacterSpikeEvent.class, this::onCharacterSpike);
        this.engine.register(CharacterStandEvent.class, this::onCharacterStand);
    }

    private void addCharacter(CharacterAppearEvent event) {
        this.engine.getWorld().addCharacter(event.getCharacter());
        try {
            this.engine.getRenderer().getMapCharacters().put(event.getCharacter().getUID(), new MapCharacter(this.engine, event.getCharacter()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void walkCharacter(CharacterWalkEvent event) {
        if (this.engine.getRenderer().getMapCharacters().get(event.getUID()).direction != event.getDirection()) {
            this.engine.getRenderer().getMapCharacters().get(event.getUID()).facePlayer(event.getDirection());
        }
        this.engine.getRenderer().walkPlayer(event.getUID(), event.getDirection(), event.getX(), event.getY());
    }

    private void removeCharacter(CharacterDisappearEvent event) {
        this.engine.getRenderer().getMapCharacters().remove(event.getUID());
        this.engine.getWorld().removeCharacter(event.getUID());
    }

    private void onCharacterAttack(CharacterAttackEvent event) {
        this.engine.getRenderer().getMapCharacters().get(event.getUID()).facePlayer(event.getDirection());
        this.engine.getRenderer().getMapCharacters().get(event.getUID()).isAttacking = true;
    }

    private void onCharacterAvatar(CharacterAvatarEvent event) {
        try {
            this.engine.getRenderer().getMapCharacters().get(event.getUID()).rerender();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onCharacterChant(CharacterChantEvent event) {
        // ???
    }

    private void onCharacterEmote(CharacterEmoteEvent event) {
        // ???
    }

    private void onCharacterEffect(CharacterEffectEvent event) {
        // ???
    }

    private void onCharacterFace(CharacterFaceEvent event) {
        this.engine.getWorld().getChars().get(event.getUID()).face(event.getDirection());
        this.engine.getRenderer().getMapCharacters().get(event.getUID()).facePlayer(event.getDirection());
    }

    private void onGroupSpell(CharacterGroupSpellEvent event) {
        // ???
    }

    private void onCharacterInstrument(CharacterInstrumentEvent event) {
        // ???
    }

    private void onCharacterOtherSpell(CharacterOtherSpellEvent event) {
        // ???
    }

    private void onCharacterSeat(CharacterSeatEvent event) {
        this.engine.getWorld().getChars().get(event.getUID()).setState(Character.State.CHAIR);
    }

    private void onCharacterSelfSpell(CharacterSelfSpellEvent event) {
        // ???
    }

    private void onCharacterSit(CharacterSitEvent event) {
        this.engine.getWorld().getChars().get(event.getUID()).setState(Character.State.FLOOR);
    }

    private void onCharacterSpike(CharacterSpikeEvent event) {
        // ???
    }

    private void onCharacterStand(CharacterStandEvent event) {
        this.engine.getWorld().getChars().get(event.getUID()).setState(Character.State.STAND);
    }


    @Override
    public void update(long delta) {
        this.engine.getRenderer().getMapCharacters().forEach((k, v) -> {
            v.update(delta);
        });
    }

    @Override
    public void onEnd() {
        this.engine.unregister(CharacterAppearEvent.class, this::addCharacter);
        this.engine.unregister(CharacterWalkEvent.class, this::walkCharacter);
        this.engine.unregister(CharacterDisappearEvent.class, this::removeCharacter);
        this.engine.unregister(CharacterAttackEvent.class, this::onCharacterAttack);
        this.engine.unregister(CharacterAvatarEvent.class, this::onCharacterAvatar);
        this.engine.unregister(CharacterChantEvent.class, this::onCharacterChant);
        this.engine.unregister(CharacterEffectEvent.class, this::onCharacterEffect);
        this.engine.unregister(CharacterEmoteEvent.class, this::onCharacterEmote);
        this.engine.unregister(CharacterFaceEvent.class, this::onCharacterFace);
        this.engine.unregister(CharacterGroupSpellEvent.class, this::onGroupSpell);
        this.engine.unregister(CharacterInstrumentEvent.class, this::onCharacterInstrument);
        this.engine.unregister(CharacterOtherSpellEvent.class, this::onCharacterOtherSpell);
        this.engine.unregister(CharacterSeatEvent.class, this::onCharacterSeat);
        this.engine.unregister(CharacterSelfSpellEvent.class, this::onCharacterSelfSpell);
        this.engine.unregister(CharacterSitEvent.class, this::onCharacterSit);
        this.engine.unregister(CharacterSpikeEvent.class, this::onCharacterSpike);
        this.engine.unregister(CharacterStandEvent.class, this::onCharacterStand);
    }
}
