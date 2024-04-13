package com.armorhud.mixin;

import com.armorhud.Client;
import com.armorhud.events.AttackEntityListener;
import com.armorhud.events.StopUsingItemListener;
import com.armorhud.mixinterface.IClientPlayerInteractionManager;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin implements IClientPlayerInteractionManager
{

	@Override
	public void cwSyncSelectedSlot()
	{
		syncSelectedSlot();
	}

	@Shadow
	private boolean breakingBlock;

	@Override
	public void setBreakingBlock(boolean breakingBlock)
	{
		this.breakingBlock = breakingBlock;
	}

	@Shadow
	private BlockPos currentBreakingPos;

	@Override
	public void setCurrentBreakingPos(BlockPos pos)
	{
		currentBreakingPos = pos;
	}

	@Shadow
	private void syncSelectedSlot()
	{

	}

	@Inject(method = "stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"), cancellable = true)
	private void onStopUsingItem(CallbackInfo ci)
	{
		StopUsingItemListener.StopUsingItemEvent stopusingitemevent = new StopUsingItemListener.StopUsingItemEvent();

		DietrichEvents2.global().postInternal(StopUsingItemListener.StopUsingItemEvent.ID, stopusingitemevent);
		if (stopusingitemevent.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci)
	{
		AttackEntityListener.AttackEntityEvent attackentityevent = new AttackEntityListener.AttackEntityEvent(player, target);

		DietrichEvents2.global().postInternal(AttackEntityListener.AttackEntityEvent.ID, attackentityevent);
		if (attackentityevent.isCancelled()) {
			ci.cancel();
		}
	}
	@Inject(method = "getReachDistance", at = @At("HEAD"), cancellable = true)
	public void getReachDistance(CallbackInfoReturnable<Float> cir) {
		if (Client.TOASTHACK.getFeatures().reachFeature.isEnabled()) {
			float reachDistance = Client.TOASTHACK.getFeatures().reachFeature.areac.getValueF();
			cir.setReturnValue(reachDistance);
		}
	}


    @Inject(method = "hasExtendedReach", at = @At("HEAD"), cancellable = true)
    public void hasExtReach(CallbackInfoReturnable<Boolean> cir) {
        if(Client.TOASTHACK.getFeatures().reachFeature.isEnabled()) {
            cir.setReturnValue(true);
        }
    }
}
