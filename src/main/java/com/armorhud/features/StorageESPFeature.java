package com.armorhud.features;

import com.armorhud.events.WorldRenderListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.utils.BlockUtils;
import com.armorhud.utils.RenderUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;

import net.minecraft.block.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

import java.awt.*;

public class StorageESPFeature extends Feature implements WorldRenderListener {

    public static boolean chest, enderchest, shulkerbox, barrel, furnace, hopper, spawner = true;

    private BooleanSetting head;

    public StorageESPFeature() {
        super("StorageESP", "Render");
        head = new BooleanSetting ("head",false,this);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(RenderWorldEvent.ID, this);
        super.onEnable();
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(RenderWorldEvent.ID, this);
        super.onDisable();
    }

    @Override
    public void onRenderWorld(RenderWorldEvent event) {
        for (BlockEntity blockEntity : BlockUtils.getBlockEntities()) {
            Box box = new Box(blockEntity.getPos());
            if (blockEntity instanceof ChestBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(55, 255, 0, 255), 0.2f);
            } else if (blockEntity instanceof EnderChestBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(123, 0, 255, 255), 0.2f);
            } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(0, 225, 255, 255), 0.2f);
            } else if (blockEntity instanceof BarrelBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(0, 225, 255, 255), 0.2f);
            } else if (blockEntity instanceof FurnaceBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(157, 203, 197, 255), 0.2f);
            } else if (blockEntity instanceof HopperBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(157, 203, 197, 255), 0.2f);
            } else if (blockEntity instanceof MobSpawnerBlockEntity) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(255, 0, 221, 255), 0.2f);
            } else if (blockEntity instanceof SkullBlockEntity && head.getValue()) {
                RenderUtils.draw3DBox(event.getStack(), box, new Color(0, 120, 0, 0), 0.2f);
            }
        }
    }
}