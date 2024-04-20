package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.setting.BooleanSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import static com.armorhud.ClientInitializer.mc;

public class SpeedBridgeFeature extends Feature implements UpdateListener {
    private boolean sneaking;
    private boolean safewalkSneaking;
    private boolean playerIsSneaking;
    private BlockPos currentBlock;
    private final BooleanSetting onlyBlocks;

    public SpeedBridgeFeature() {
        super("Speed Bridge", "Misc");
        this.sneaking = false;
        this.safewalkSneaking = false;
        this.playerIsSneaking = false;
        this.onlyBlocks = new BooleanSetting("Only Blocks", true, this);
        addSetting(onlyBlocks);
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        if (mc.player != null) {
            KeyBinding.updatePressedStates();
            mc.options.sneakKey.setPressed(false);
            DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
        }
    }

    @Override
    public void onUpdate() {
        if (mc.world != null && mc.player != null) {
            double deltaY = mc.player.prevY - mc.player.getY();
            if (deltaY > 0.4 || mc.player.getAbilities().flying || (onlyBlocks.getValue() && !(mc.player.getMainHandStack().getItem() instanceof BlockItem))) {
                return;
            }
            BlockPos thisBlock = new BlockPos(MathHelper.floor(mc.player.getX()), MathHelper.floor(mc.player.getY() - 1), MathHelper.floor(mc.player.getZ()));
            if (currentBlock == null || !currentBlock.equals(thisBlock)) {
                currentBlock = thisBlock;
            }
            safewalkSneaking = mc.world.getBlockState(currentBlock).isAir();
            checkSneak();
        }
    }

    private void checkSneak() {
        if (!playerIsSneaking) {
            if (mc.player.isOnGround()) {
                sneaking = safewalkSneaking;
                mc.options.sneakKey.setPressed(sneaking);
            } else {
                mc.options.sneakKey.setPressed(false);
            }
        } else {
            mc.options.sneakKey.setPressed(true);
        }
    }
}
