package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.setting.BooleanSetting;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import com.armorhud.setting.IntegerSetting;

import static com.armorhud.Client.MC;

public class AutoPotFeature extends Feature implements UpdateListener {
    private final BooleanSetting fakeCPS = new BooleanSetting("fakeCPS", true, this);
    private final IntegerSetting health = new IntegerSetting("Health",  5, this);
    private final IntegerSetting delay = new IntegerSetting("Delay",  4, this);
    private float targetPitch = 90.0f;
    private float pitchStep;
    private int potSlot;
    private int preSlot;
    private int ticksAfterPotion = 0;

    public AutoPotFeature() {
        super("AutoPot", "Misc");
    }

    @Override
    public void onEnable() {
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        if (MC.currentScreen != null) {
            return;
        }

        ClientPlayerEntity player = Client.MC.player;
        IMouse mouse = (IMouse) MC.mouse;

        if (player.getHealth() <= health.getValue()) {
            if (hotbarContainsPotions()) {
                if (ticksAfterPotion == 0) {
                    preSlot = player.getInventory().selectedSlot;
                    pitchStep = (targetPitch - player.getPitch()) / delay.getValue();
                }

                if (ticksAfterPotion < delay.getValue()) {
                    player.setPitch(player.getPitch() + pitchStep);
                    ticksAfterPotion++;
                } else if (ticksAfterPotion == delay.getValue()) {
                    potSlot = findPotionSlot();
                    if (potSlot != -1) {
                        sendSlotUpdate(potSlot);
                        player.getInventory().selectedSlot = potSlot;
                        ActionResult result = MC.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        if (result.isAccepted() && result.shouldSwingHand()) {
                            player.swingHand(Hand.MAIN_HAND);

                            if (this.fakeCPS.getValue()) {
                                mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 1, 1, 0);
                                mouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), 0, 0, 0);
                            }
                        }
                    }
                    ticksAfterPotion++;
                } else {
                    player.getInventory().selectedSlot = preSlot;
                    ticksAfterPotion = 0;
                }
            }
        } else {
            ticksAfterPotion = 0;
        }
    }


    private void sendSlotUpdate(int slotIndex) {
        MC.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slotIndex));
    }

    private boolean hotbarContainsPotions() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Client.MC.player.getInventory().getStack(i);
            if (itemStack.getItem() == Items.SPLASH_POTION) {
                return true;
            }
        }
        return false;
    }

    private int findPotionSlot() {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = Client.MC.player.getInventory().getStack(i);
            if (itemStack.getItem() == Items.SPLASH_POTION) {
                return i;
            }
        }
        return -1;
    }
}
