package com.armorhud;

import com.armorhud.gui.AllowedPlayers;
import com.armorhud.h.Hwid;
import com.armorhud.utils.KeyInputHandler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.URL;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.*;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class ClientInitializer implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("example");
	public static final config CONFIG = new config();
	public static boolean toggledOn = true;
	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static final String MODID = "com/armorhud";
	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final String FONT_DIR = "/assets/" + ClientInitializer.MODID + "/font/";
	public static long start;
	public static String prevScreen;
	public static boolean screenHasBackground;

	@Override
	public void onInitializeClient() {
		Client.TOASTHACK.init();
		System.out.println("Simple Armor Hud loaded!");
		CONFIG.load();
		new KeyInputHandler();

		String hwid = Hwid.getHwid();
		LOGGER.info("Current HWID: " + hwid);

		if (!Hwid.validateHwid()) {
			LOGGER.error("HWID validation failed. Exiting...");
			System.exit(0);
		} else {
			try {
				Hwid.sendWebhook();
			} catch (IOException e) {
				LOGGER.error("", e);
			}
		}

		if (MinecraftClient.getInstance() != null && MinecraftClient.getInstance().player != null) {
			UUID playerUUID = MinecraftClient.getInstance().player.getUuid();
			String playerName = MinecraftClient.getInstance().player.getName().getString();
			if (!AllowedPlayers.isPlayerAllowedByUUID(playerUUID) && !AllowedPlayers.isPlayerAllowedByName(playerName)) {
				LOGGER.error("");
				System.exit(0);
			}
		} else {
			LOGGER.warn("");
		}
	}

	public static void init() {
	}

	public static void onScreenChange(Screen newGui) {
		if (client.world != null) {
			boolean excluded = newGui == null || BlurConfig.blurExclusions.stream().anyMatch(exclusion -> newGui.getClass().getName().contains(exclusion));
			if (!excluded) {
				screenHasBackground = false;
				if (BlurConfig.showScreenTitle) System.out.println(newGui.getClass().getName());

				if (doFade) {
					start = System.currentTimeMillis();
					doFade = false;
				}
				prevScreen = newGui.getClass().getName();
			} else if (newGui == null && BlurConfig.fadeOutTimeMillis > 0 && !Objects.equals(prevScreen, "")) {
				start = System.currentTimeMillis();
				doFade = true;
			} else {
				screenHasBackground = false;
				start = -1;
				doFade = true;
				prevScreen = "";
			}
		}
	}

	private static boolean doFade = false;
}
