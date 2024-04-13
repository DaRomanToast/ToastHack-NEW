package com.armorhud.utils;

import net.minecraft.client.network.OtherClientPlayerEntity;

import java.util.UUID;

import static com.armorhud.Client.MC;

public class FakePlayerEntityUtil extends OtherClientPlayerEntity {

    public FakePlayerEntityUtil() {
        super(MC.world, MC.player.getGameProfile());
        copyFrom(MC.player);
        getPlayerListEntry();
        dataTracker.set(PLAYER_MODEL_PARTS, MC.player.getDataTracker().get(PLAYER_MODEL_PARTS));
        setUuid(UUID.randomUUID());
    }

    public void add() {
        unsetRemoved();
        assert MC.world != null;
        // Spawn the entity into the world
        MC.world.spawnEntity(this);
    }


    public void remove() {
        assert MC.world != null;
        MC.world.removeEntity(this.getId(), RemovalReason.DISCARDED);
    }

}