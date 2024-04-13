package com.armorhud.gui.screen;

import com.armorhud.Client;
import com.armorhud.gui.Color;
import net.minecraft.client.gui.DrawContext;
import com.armorhud.keybind.Keybind;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import com.armorhud.text.VanillaTextRenderer;

import java.util.ArrayList;

public class KeybindScreen extends Screen
{

	private final Screen prevScreen;
	private final ArrayList<ButtonWidget> editButtons = new ArrayList<>();

	public KeybindScreen(Screen prevScreen)
	{
		super(Text.of(""));
		this.prevScreen = prevScreen;
	}

	@Override
	protected void init()
	{
		ButtonWidget doneButton = ButtonWidget.builder(Text.of("Done"), b -> Client.MC.setScreen(prevScreen)).dimensions(width / 2 - 100, height - 50, 200, 20).build();		addDrawableChild(doneButton);
		addDrawableChild(doneButton);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);

		int x = width / 3;
		int y = 50;
		ArrayList<Keybind> keybinds = Client.TOASTHACK.getKeybindManager().getAllKeybinds();
		for (Keybind keybind : keybinds)
		{
			Color color = new Color(128, 128, 128);
			VanillaTextRenderer.INSTANCE.render(keybind.getName(), x, y, color);
			y += 32;
		}
	}

}
