package com.armorhud.mixin;

import com.armorhud.Client;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import com.armorhud.features.AntiCrystalBounceFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin({EndCrystalItem.class})
public class EndCrystalItemMixin {
    public EndCrystalItemMixin() {
    }
    private Vec3d getPlayerLookVec(PlayerEntity player) {
        float f = 0.017453292F;
        float pi = 3.1415927F;
        float f1 = MathHelper.cos(-player.getYaw() * f - pi);
        float f2 = MathHelper.sin(-player.getYaw() * f - pi);
        float f3 = -MathHelper.cos(-player.getPitch() * f);
        float f4 = MathHelper.sin(-player.getPitch() * f);
        return (new Vec3d((double)(f2 * f3), (double)f4, (double)(f1 * f3))).normalize();
    }

    private Vec3d getClientLookVec() {
        return this.getPlayerLookVec(Client.TOASTHACK.getClient().player	);
    }

    private boolean isBlock(Block block, BlockPos pos) {
        return this.getBlockState(pos).getBlock() == block;
    }

    private BlockState getBlockState(BlockPos pos) {
        return Client.TOASTHACK.getClient().world.getBlockState(pos);
    }

    private boolean canPlaceCrystalServer(BlockPos block) {
        BlockState blockState = Client.TOASTHACK.getClient().world.getBlockState(block);
        if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) {
            return false;
        } else {
            BlockPos blockPos2 = block.up();
            if (! Client.TOASTHACK.getClient().world.isAir(blockPos2)) {
                return false;
            } else {
                double d = (double)blockPos2.getX();
                double e = (double)blockPos2.getY();
                double f = (double)blockPos2.getZ();
                List<Entity> list = Client.TOASTHACK.getClient().world.getOtherEntities((Entity)null, new Box(d, e, f, d + 1.0, e + 2.0, f + 1.0));
                return list.isEmpty();
            }
        }
    }

    @Inject(
            method = {"useOnBlock"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private void modifyDecrementAmount(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (AntiCrystalBounceFeature.enabled) {
            ItemStack mainHandStack = Client.TOASTHACK.getClient().player.getMainHandStack();
            if (mainHandStack.isOf(Items.END_CRYSTAL)) {
                Vec3d camPos = Client.TOASTHACK.getClient().player.getEyePos();
                BlockHitResult blockHit = Client.TOASTHACK.getClient().world.raycast(new RaycastContext(camPos, camPos.add(this.getClientLookVec().multiply(4.5)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, Client.TOASTHACK.getClient().player	));
                if (this.isBlock(Blocks.OBSIDIAN, blockHit.getBlockPos()) || this.isBlock(Blocks.BEDROCK, blockHit.getBlockPos())) {
                    HitResult hitResult = Client.TOASTHACK.getClient().crosshairTarget;
                    if (hitResult instanceof BlockHitResult) {
                        BlockHitResult hit = (BlockHitResult)hitResult;
                        BlockPos block = hit.getBlockPos();
                        if (this.canPlaceCrystalServer(block)) {
                            context.getStack().decrement(-1);
                        }
                    }
                }
            }

        }
    }

}

