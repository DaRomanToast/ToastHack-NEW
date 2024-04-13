package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.EnumSetting;
import com.armorhud.utils.InventoryUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;

public class AutoGappleFeature extends Feature implements UpdateListener {
    @Override
    public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }
    private EnumSetting<HealthMode> healthModeSetting;
    private MinecraftClient mc;

    public AutoGappleFeature() {
        super("AutoGapple", "Combat");
        mc = MinecraftClient.getInstance();
        healthModeSetting = new EnumSetting<>("Health Mode", HealthMode.values(), HealthMode.HUNDRED, this);
        addSetting(healthModeSetting);
    }

    @Override
    public void onUpdate() {
        if (!isEnabled()) return;

        if (Client.MC.player.isUsingItem()) return;

        HealthMode mode = healthModeSetting.getValue();
        float healthThreshold = mode == HealthMode.HUNDRED ? 0.95f * Client.MC.player.getMaxHealth() : 13;

        if (Client.MC.player.getHealth() < healthThreshold) {
            consumeGapple();
        }
    }

    private void consumeGapple() {
        boolean hasGapple = InventoryUtils.hasItemInHotbar(Items.GOLDEN_APPLE);
        boolean hasEgap = InventoryUtils.hasItemInHotbar(Items.ENCHANTED_GOLDEN_APPLE);

        if (hasEgap) {
            InventoryUtils.selectItemFromHotbar(Items.ENCHANTED_GOLDEN_APPLE);
        } else if (hasGapple) {
            InventoryUtils.selectItemFromHotbar(Items.GOLDEN_APPLE);
        } else {
            return;
        }

        IMouse mouse = (IMouse) Client.MC.mouse;
        mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 0, 0);
        mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 1, 0);
    }


    public enum HealthMode {
        HUNDRED,
        TEN
    }

}
