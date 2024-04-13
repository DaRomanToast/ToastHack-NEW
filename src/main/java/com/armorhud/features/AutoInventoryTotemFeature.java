package com.armorhud.features;

import com.armorhud.Client;
import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import com.armorhud.keybind.Keybind;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;

public class AutoInventoryTotemFeature extends Feature implements UpdateListener {

    private final BooleanSetting autoSwitch = new BooleanSetting("autoSwitch",true,this);

    private final IntegerSetting delay = new IntegerSetting("delay",0,this);

    private final IntegerSetting totemSlot = new IntegerSetting("totemSlot",8,this);

    private final BooleanSetting forceTotem = new BooleanSetting("forceTotem",false,this);


    private final BooleanSetting activateOnKey = new BooleanSetting("Activate On Key",false, this);

    private final Keybind activateKeybind = new Keybind(
            "AutoInventoryTotem_activateKeybind",
            GLFW.GLFW_KEY_C,
            false,
            false,
            "");

    public AutoInventoryTotemFeature() {
        super("AutoInventoryTotem", "Combat");
    }

    private int invClock = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        invClock = -1;
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate() {
        if (!(Client.MC.currentScreen instanceof InventoryScreen)) {
            invClock = -1;
            return;
        }
        if (invClock == -1)
            invClock = delay.getValue();
        if (invClock > 0) {
            invClock--;
            return;
        }
        PlayerInventory inv = Client.MC.player.getInventory();
        if (autoSwitch.getValue()) {
            inv.selectedSlot = totemSlot.getValue() - 1;
            if (activateOnKey.getValue() && !activateKeybind.isDown())
                return;
            if (inv.offHand.get(0).getItem() != Items.TOTEM_OF_UNDYING) {
                int slot = findTotemSlot();
                if (slot != -1) {
                    Client.MC.interactionManager.clickSlot(((InventoryScreen) Client.MC.currentScreen).getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, Client.MC.player);
                    return;
                }
            }
            ItemStack mainHand = inv.main.get(inv.selectedSlot);
            if (mainHand.isEmpty() ||
                    forceTotem.getValue() && mainHand.getItem() != Items.TOTEM_OF_UNDYING) {
                int slot = findTotemSlot();
                if (slot != -1) {
                    Client.MC.interactionManager.clickSlot(((InventoryScreen) Client.MC.currentScreen).getScreenHandler().syncId, slot, inv.selectedSlot, SlotActionType.SWAP, Client.MC.player);
                }
            }
        }
    }
    private int findTotemSlot() {
        PlayerInventory inv = Client.MC.player.getInventory();
        for (int i = 9; i < 36; i++) {
            if (inv.main.get(i).getItem() == Items.TOTEM_OF_UNDYING)
                return i;
        }
        return -1;
    }
}