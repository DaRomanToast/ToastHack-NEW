/*
 * Copyright (c) Shadow client, Saturn5VFive and contributors 2022. All rights reserved.
 */

package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.events.WorldRenderListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.EnumSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.utils.RenderUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.block.*;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.armorhud.utils.RenderUtils.draw3DBox;

public class AntiAntiXrayFeature extends Feature implements UpdateListener, WorldRenderListener {

    final Queue<BlockPos> toScan = new ArrayDeque<>();
    final List<BlockPos> renders = new ArrayList<>();
    final List<BlockPos> visitsAfter = new ArrayList<>();
    final IntegerSetting range = new IntegerSetting("Range",64,this);
    final IntegerSetting customYSize = new IntegerSetting("Custom Y size",-1,this);
    final IntegerSetting skipDistance = new IntegerSetting("Skip distance",0,this);
    final EnumSetting<Mode> mode = new EnumSetting<>("mode",  Mode.values(), Mode.Ores, this);
    final IntegerSetting blocksPerTick = new IntegerSetting("Blocks per tick",1,this);
    final IntegerSetting delay = new IntegerSetting("Delay",0,this);
    final BooleanSetting showAura = new BooleanSetting("Show aura",false,this);
    final BooleanSetting exposedOnly = new BooleanSetting("Exposed Ore",false,this);
    //    final SliderValue     customYSize   = (SliderValue) this.config.create("Custom Y size", -1, -1, 255, 0).description("Custom Y size of scanner box (-1 to disable)");
    public AntiAntiXrayFeature()
    {
        super("AntiXRayBypass", "Render");
    }
    List<BlockPos> permanentToScan = new ArrayList<>();
    Vec3d startPos;
    int scanned = 0;
    int delayPassed = 0;
    BlockPos latestGoal = null;

    boolean isBlockValid(Block b) {
        return switch (mode.getValue()) {
            case Ores -> AntiAntiXrayFeature.blockApplicable(b);
            case Stone -> b == Blocks.STONE;
            case Diamond -> b == Blocks.DIAMOND_ORE;
            case Redstone -> b == Blocks.REDSTONE_ORE;
            case Iron -> b == Blocks.IRON_ORE;
            case Netherite -> b == Blocks.ANCIENT_DEBRIS;
            case Everything -> true;
        };
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(RenderWorldEvent.ID, this);
        DietrichEvents2.global().subscribe(UpdateEvent.ID, this);
        if (Client.MC.player == null || Client.MC.world == null) {
            return;
        }
        toScan.clear();
        scanned = 0;
        startPos = Client.MC.player.getPos();
        BlockPos ppos = Client.MC.player.getBlockPos();
        Vec3d lastPos = Vec3d.ZERO;
        int rangeMid = (int) (range.getValue() / 2);
        int ry = customYSize.getValue() == -1 ? rangeMid : (int) (customYSize.getValue() / 2);
        for (int y = ry; y > -ry; y--) {
            for (int x = -rangeMid; x < rangeMid; x++) {
                for (int z = -rangeMid; z < rangeMid; z++) {
                    BlockPos current = ppos.add(x, y, z);
                    assert Client.MC.world != null;
                    BlockState bs = Client.MC.world.getBlockState(current);
                    Vec3d currentPos = new Vec3d(current.getX(), current.getY(), current.getZ());
                    if (!bs.isAir() && lastPos.distanceTo(currentPos) >= skipDistance.getValue()) {
                        if (isBlockValid(bs.getBlock())) {
                            toScan.add(current);
                            lastPos = currentPos;
                        }
                    }
                }
            }
        }
        permanentToScan = new ArrayList<>(toScan);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(RenderWorldEvent.ID, this);
        DietrichEvents2.global().unsubscribe(UpdateEvent.ID, this);
        visitsAfter.clear();
        latestGoal = null;
    }

    @Override
    public void onUpdate() {
        if (Client.MC.player == null || Client.MC.getNetworkHandler() == null) {
            return;
        }
        if (toScan.size() == 0) {
            System.out.println("AntiAntiXray Is Done");
            return;
        }
        if (delayPassed > delay.getValue()) {
            delayPassed = 0;
        } else {
            delayPassed++;
            return;
        }
        renders.clear();
        for (int i = 0; i < blocksPerTick.getValue(); i++) {
            if (toScan.size() == 0) {
                break;
            }
            BlockPos current = toScan.poll();
            renders.add(current);
            scanned++;

            PlayerActionC2SPacket p = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, current, Direction.DOWN);
            Client.MC.getNetworkHandler().sendPacket(p);
        }
    }

    @Override
    public void onRenderWorld(WorldRenderListener.RenderWorldEvent event) {
        for (BlockPos latestScan : renders.toArray(new BlockPos[0])) {
            draw3DBox(event.getStack(), new Box(latestScan), Color.CYAN, 1.0f);
        }
        double mid = (double) this.range.getValue() / 2;
        Vec3d s = new Vec3d(mid, mid, mid);
        Vec3d ppOrigin = startPos;
        Vec3d boxOrigin = ppOrigin.subtract(s);
        RenderUtils.R3D.renderOutline(boxOrigin, s.multiply(2), Color.CYAN, event.getStack());

        if (customYSize.getValue() != -1) {
            Vec3d sub = new Vec3d(mid, customYSize.getValue() / 2d, mid);
            RenderUtils.R3D.renderOutline(ppOrigin.subtract(sub), sub.multiply(2), Color.CYAN, event.getStack());
        }

        if (latestGoal != null) {
            RenderUtils.R3D.renderLine(RenderUtils.R3D.getCrosshairVector(), new Vec3d(latestGoal.getX(), latestGoal.getY(), latestGoal.getZ()).add(0.5, 0.5, 0.5), Color.CYAN, event.getStack());
        }

        if (renders.size() == 0 || !showAura.getValue()) {
            return;
        }
        double maxDist = 5;
        for (BlockPos bp : permanentToScan) {
            Vec3d vv = new Vec3d(bp.getX(), bp.getY(), bp.getZ());
            double d = 0;
            int i = 0;
            for (BlockPos render : renders) {
                Vec3d e = new Vec3d(render.getX(), render.getY(), render.getZ());
                double ee = vv.distanceTo(e) / maxDist;
                if (ee > 1) {
                    continue;
                }
                d += ee;
                i++;
            }
            if (i == 0) {
                continue;
            }
            d /= i;
            d = Math.abs(1 - d);
            Color c = Color.getHSBColor((float) d, 0.6f, 1f);
            c = RenderUtils.R3D.modify(c, -1, -1, -1, (int) Math.floor(d * 255d));
        }
    }

    public enum Mode {
        Ores, Stone, Diamond, Redstone, Iron, Netherite, Everything
    }
    public static boolean blockApplicable(Block block) {
        boolean c1 = block == Blocks.CHEST || block == Blocks.FURNACE || block == Blocks.END_GATEWAY || block == Blocks.COMMAND_BLOCK || block == Blocks.ANCIENT_DEBRIS;
        boolean c2 = block instanceof ExperienceDroppingBlock|| block instanceof RedstoneOreBlock;
        return c1 || c2;
    }
}