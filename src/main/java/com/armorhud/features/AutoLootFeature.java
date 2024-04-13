package com.armorhud.features;

import com.armorhud.events.UpdateListener;
import com.armorhud.feature.Feature;
import com.armorhud.keybind.Keybind;
import com.armorhud.setting.BooleanSetting;
import com.armorhud.setting.IntegerSetting;
import com.armorhud.setting.KeybindSetting;
import com.armorhud.utils.InventoryUtils;
import de.florianmichael.dietrichevents2.DietrichEvents2;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static com.armorhud.ClientInitializer.mc;

public class AutoLootFeature extends Feature implements UpdateListener
{

    public IntegerSetting minTotems = new IntegerSetting("min totems", 6, this);
    public IntegerSetting minPearls = new IntegerSetting("min pearls", 64, this);

    public  BooleanSetting totemFirst  = new BooleanSetting ("totemFirst", false, this);
    public IntegerSetting dropInterval = new IntegerSetting("dropspeed", 0, this);
    private Keybind activateKeybind = new Keybind(
            "AutoLootYeeter_activateKeybind",
            GLFW.GLFW_KEY_X,
            false,
            false,
            "");

    private KeybindSetting activateKeybindSetting;

    private int dropClock = 0;

    public AutoLootFeature()
    {
        super("AutoLoot", "Misc");
        this.activateKeybindSetting = new KeybindSetting("Activate AutoLoot", this, new Keybind("AutoLoot Keybind", GLFW.GLFW_KEY_X, true, false, ""));
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
        DietrichEvents2.global().subscribe(UpdateListener.UpdateEvent.ID, this);
        dropClock = 0;
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        DietrichEvents2.global().unsubscribe(UpdateListener.UpdateEvent.ID, this);
    }

    @Override
    public void onUpdate()
    {
        if (dropClock != 0)
        {
            dropClock--;
            return;
        }
        if (!(mc.currentScreen instanceof InventoryScreen))
            return;
        if (this.activateKeybindSetting.getValue().isDown())
            return;
        if (!looting())
            return;
        int slot = findSlot();
        if (slot == -1)
            return;
        dropClock = dropInterval.getValue();
        mc.interactionManager.clickSlot(((InventoryScreen) mc.currentScreen).getScreenHandler().syncId, slot, 1, SlotActionType.THROW, mc.player);
    }

    private boolean looting()
    {
        List<Entity> collidedEntities = mc.world.getOtherEntities(mc.player, mc.player.getBoundingBox().expand(1, 0.5, 1).expand(1.0E-7D));
        for (Entity e : collidedEntities)
        {
            if (e instanceof ItemEntity itemStack)
            {
                Item item = itemStack.getStack().getItem();
                if (item != Items.TOTEM_OF_UNDYING && item != Items.ENDER_PEARL)
                {
                    if (item == Items.END_CRYSTAL ||
                            item == Items.RESPAWN_ANCHOR ||
                            item == Items.GOLDEN_APPLE)
                        return true;
                    if (item instanceof ToolItem toolItem)
                    {
                        if (toolItem.getMaterial() == ToolMaterials.NETHERITE ||
                                toolItem.getMaterial() == ToolMaterials.DIAMOND)
                            return true;
                    }
                    if (item instanceof ArmorItem armorItem)
                    {
                        if (armorItem.getMaterial() == ArmorMaterials.NETHERITE ||
                                armorItem.getMaterial() == ArmorMaterials.DIAMOND)
                            return true;
                    }
                }
            }
        }
        return false;
    }

    private int findSlot()
    {
        if (totemFirst.getValue())
        {
            int totemSlot = findTotemSlot();
            if (totemSlot == -1)
                return findPearlSlot();
            return totemSlot;
        }
        int pearlSlot = findPearlSlot();
        if (pearlSlot == -1)
            return findTotemSlot();
        return pearlSlot;
    }

    private int findPearlSlot()
    {
        PlayerInventory inv = mc.player.getInventory();
        int pearlCount = InventoryUtils.countItem(Items.ENDER_PEARL);
        int fewestPearlSlot = -1;
        for (int i = 9; i < 36; i++)
        {
            ItemStack itemStack = inv.main.get(i);
            if (itemStack.getItem() == Items.ENDER_PEARL)
            {
                if (fewestPearlSlot == -1 ||
                        itemStack.getCount() < inv.main.get(fewestPearlSlot).getCount())
                {
                    fewestPearlSlot = i;
                }
            }
        }
        if (fewestPearlSlot == -1)
            return -1;
        if (pearlCount - inv.main.get(fewestPearlSlot).getCount() >= minPearls.getValue())
        {
            return fewestPearlSlot;
        }
        return -1;
    }

    private int findTotemSlot()
    {
        PlayerInventory inv = mc.player.getInventory();
        int totemCount = InventoryUtils.countItem(Items.TOTEM_OF_UNDYING);
        if (totemCount <= minTotems.getValue())
            return -1;
        for (int i = 9; i < 36; i++)
        {
            ItemStack itemStack = inv.main.get(i);
            if (itemStack.getItem() == Items.TOTEM_OF_UNDYING)
            {
                return i;
            }
        }
        return -1;
    }

}
