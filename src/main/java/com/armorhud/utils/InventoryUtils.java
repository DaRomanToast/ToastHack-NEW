package com.armorhud.utils;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

import static com.armorhud.Client.MC;

public enum InventoryUtils {
	;
	public static final int MAIN_END = 35;

	public static boolean selectItemFromHotbar(Predicate<Item> item) {
		PlayerInventory inv = MC.player.getInventory();

		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inv.getStack(i);
			if (!item.test(itemStack.getItem()))
				continue;
			inv.selectedSlot = i;
			return true;
		}

		return false;
	}

	public static boolean selectItemFromHotbar(Item item) {
		return selectItemFromHotbar(i -> i == item);
	}

	public static boolean hasItemInHotbar(Predicate<Item> item) {
		PlayerInventory inv = MC.player.getInventory();

		for (int i = 0; i < 9; i++) {
			ItemStack itemStack = inv.getStack(i);
			if (item.test(itemStack.getItem()))
				return true;
		}
		return false;
	}

	public static int countItem(Predicate<Item> item) {
		PlayerInventory inv = MC.player.getInventory();

		int count = 0;

		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = inv.getStack(i);
			if (item.test(itemStack.getItem()))
				count += itemStack.getCount();
		}

		return count;
	}

	public static boolean hasItemInInventory(Predicate<Item> item) {
		PlayerInventory inv = MC.player.getInventory();

		for (int i = 0; i < 36; i++) {
			ItemStack itemStack = inv.getStack(i);
			if (item.test(itemStack.getItem()))
				return true;
		}
		return false;

	}
	public static boolean hasItemInSlot(int slot, Predicate<Item> item) {
		PlayerInventory inv = MC.player.getInventory();
		if (slot < 0 || slot >= inv.size()) {
			return false; // Invalid slot
		}
		ItemStack itemStack = inv.getStack(slot);
		return item.test(itemStack.getItem());
	}

	public static boolean hasItemInSlot(int slot, Item item) {
		return hasItemInSlot(slot, i -> i == item);
	}

	public static boolean hasItemInInventory(Item item) {
		return hasItemInInventory(i -> i == item);
	}

	public static int countItem(Item item) {
		return countItem(i -> i == item);
	}

	public static boolean hasItemInHotbar(Item item) {
		return hasItemInHotbar(i -> i == item);
	}

}