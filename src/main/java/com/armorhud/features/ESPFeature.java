package com.armorhud.features;

import java.util.List;
import java.util.ArrayList;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.entity.player.PlayerEntity;
import com.armorhud.setting.BooleanSetting;
import net.minecraft.entity.Entity;

public class ESPFeature extends Feature implements UpdateListener {
    private List<Entity> targetEntities = new ArrayList<>();
    private final BooleanSetting invisibles = new BooleanSetting("Invisibles",  true, this);

    public ESPFeature() {
        super("Chams", "Render");
        get = this;
    }

    @Override
    public void onUpdate() {
        assert Client.MC.world != null;
        for (Entity e : Client.MC.world.getEntities()) {
            if (e instanceof PlayerEntity) {
                e.setGlowing(true);
            }
        }
        if (invisibles.isEnabled()) {
            Client.MC.world.getEntities().forEach(entity -> {
                if (entity.isInvisible() && !targetEntities.contains(entity)) {
                    targetEntities.add(entity);
                    entity.setInvisible(false);
                }
            });
        }
    }
    @Override
    public void onEnable() {
        assert Client.MC.world != null;
        for (Entity e : Client.MC.world.getEntities()) {
            if (e instanceof PlayerEntity && !e.isGlowing()) {
                e.setGlowing(true);
            }
        }
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        assert Client.MC.world != null;
        for (Entity e : Client.MC.world.getEntities()) {
            if (e instanceof PlayerEntity && !e.isGlowing()) {
                e.setGlowing(false);
            }
        }
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        if (invisibles.isEnabled()) {
            targetEntities.forEach(entity -> {entity.setInvisible(true);});
            targetEntities.clear();
        }
    super.onDisable();
    }


    public boolean shouldRenderEntity(Entity entity) {
        if (!isEnabled()) return false;
        if (entity == null) return false;
        
        return true;
    }

    public static ESPFeature get;
}
