package com.armorhud.gui.screen;

import com.armorhud.Client;
import com.armorhud.gui.Color;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import com.armorhud.text.VanillaTextRenderer;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;


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