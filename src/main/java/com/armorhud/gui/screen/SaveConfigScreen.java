package com.armorhud.gui.screen;

import com.armorhud.Client;
import com.armorhud.gui.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import com.armorhud.text.VanillaTextRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.armorhud.Client.TOASTHACK;

public class SaveConfigScreen extends Screen {

	private final Screen prevScreen;
	private ButtonWidget doneButton;
	private TextFieldWidget textWidget;

	public SaveConfigScreen(Screen prevScreen) {
		super(Text.literal(""));
		this.prevScreen = prevScreen;
	}

	@Override
	protected void init() {
		doneButton = ButtonWidget.builder(Text.of("Done"), b -> done()).dimensions(width / 2 - 100, height - 50, 200, 20).build();
		addDrawableChild(doneButton);

		textWidget = new TextFieldWidget(Client.MC.textRenderer, width / 2 - 200, height / 3, 400, 20, Text.of(""));
		textWidget.setMaxLength(256);
		textWidget.setEditable(true);
		textWidget.setVisible(true);
		textWidget.setFocusUnlocked(true);
		addSelectableChild(textWidget);
		setInitialFocus(textWidget);
	}

	private void done() {
		String configName = textWidget.getText();
		Path configPath = TOASTHACK.getConfigDirectory().resolve(configName + ".th");

		Client.TOASTHACK.getFeatures().saveAsFile(configPath.toString());

		saveFriends(TOASTHACK.getConfigDirectory().resolve(configName + "_friends.txt"));

		if (prevScreen instanceof ConfigScreen) {
			ConfigScreen configScreen = (ConfigScreen) prevScreen;
			configScreen.setConfigList(TOASTHACK.getConfigDirectory().toFile().listFiles());
		}

		Client.MC.setScreen(prevScreen);
	}

	private void saveFriends(Path filePath){
		try {
			HashSet<UUID> friends = TOASTHACK.getFriendList().getFriends();
			List<String> lines = friends.stream().map(UUID::toString).collect(Collectors.toList());
			Files.write(filePath, lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		textWidget.tick();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		textWidget.render(context, mouseX, mouseY, delta);
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		Color color = new Color(255, 255, 255);
		VanillaTextRenderer.INSTANCE.render("Save config as ...", (double) width / 2, (double) height / 3 - 20, color);
	}
}
