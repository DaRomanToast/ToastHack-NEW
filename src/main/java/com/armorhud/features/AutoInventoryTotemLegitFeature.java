package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.utils.AccessorUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import com.armorhud.setting.IntegerSetting;

public class AutoInventoryTotemLegitFeature extends Feature implements UpdateListener {

    private final IntegerSetting delay = new IntegerSetting("delay",  0, this);


    private final IntegerSetting totemSlot = new IntegerSetting("totemSlot",  9, this);



    public AutoInventoryTotemLegitFeature() {
        super("LegitRetotem", "Combat");
    }

    private int totemClock = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);

        totemClock = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }


    @Override
    public void onUpdate() {
        if (!(Client.MC.currentScreen instanceof InventoryScreen)) {
            totemClock = 0;
            return;
        }

        InventoryScreen invScreen = (InventoryScreen) Client.MC.currentScreen;

        Slot focusedSlot = getFocusedSlot();
        if (focusedSlot == null) {
            totemClock = 0;
            return;
        }

        int slotIndex = focusedSlot.getIndex();
        int totemHotbarIndex = totemSlot.getValue() - 1;
        if (slotIndex < 9 || slotIndex >= 36) {
            totemClock = 0;
            return;
        }

        if (slotIndex == totemHotbarIndex) {
            totemClock = 0;
            return;
        }
        if (isTotem(slotIndex) && !isTotem(totemHotbarIndex)) {
            if (totemClock < delay.getValue()) {
                totemClock++;
                return;
            }
            Client.MC.interactionManager.clickSlot(
                    invScreen.getScreenHandler().syncId,
                    slotIndex,
                    totemHotbarIndex,
                    SlotActionType.SWAP,
                    Client.MC.player
            );
            totemClock = 0;
            return;
        }

        if (!Client.MC.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && isTotem(slotIndex)) {
            if (totemClock < delay.getValue()) {
                totemClock++;
                return;
            }

            Client.MC.interactionManager.clickSlot(
                    invScreen.getScreenHandler().syncId,
                    slotIndex,
                    40,
                    SlotActionType.SWAP,
                    Client.MC.player
            );
            totemClock = 0;
        }
    }

    private Slot getFocusedSlot() {
        final Screen screen = MinecraftClient.getInstance().currentScreen;
        final HandledScreen<?> gui = (HandledScreen<?>) screen;
        final Slot slot = AccessorUtils.getSlotUnderMouse(gui);
        return slot;
    }

    private boolean isTotem(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= Client.MC.player.getInventory().size()) {
            return false;
        }
        return Client.MC.player.getInventory().main.get(slotIndex).isOf(Items.TOTEM_OF_UNDYING);
    }
}