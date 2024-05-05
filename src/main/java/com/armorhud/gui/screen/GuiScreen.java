package com.armorhud.gui.screen;

import com.armorhud.setting.Setting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.gui.DrawContext;
import com.armorhud.feature.Feature;
import com.armorhud.feature.FeatureList;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import com.armorhud.gui.Color;
import com.armorhud.text.VanillaTextRenderer;

import java.util.*;

import static com.armorhud.Client.TOASTHACK;
import static com.armorhud.Client.MC;

public class GuiScreen extends Screen {

	private static final Color GRAY = new Color(140, 140, 140);
	private static final Color WHITE = new Color(255, 255, 255);
	private static final Color HOVERED = new Color(210, 210, 210);

	private static final int WIDGET_WIDTH = 60;
	private static final int WIDGET_HEIGHT = 10;
	private static final int INTER_WIDGET_PADDING = 10;
	private TextFieldWidget searchText;
	private float time = 0;

	private final ArrayList<Feature> foundFeatures = new ArrayList<>();
	private static final Map<String, Integer> persistentCategoryPositions = new HashMap<>();
	private static final Map<String, Boolean> persistentCategoryVisibility = new HashMap<>();

	private final Map<String, Boolean> categoryVisibility = new HashMap<>();
	private final Map<String, Integer> categoryPositions = new HashMap<>();

	private final TreeMap<Feature, int[]> feature2pos = new TreeMap<>(Comparator.comparing(Feature::getName));
	private final Map<String, ArrayList<Feature>> categorizedFeatures = new HashMap<>();

	private String currentlyDraggedCategory = null;
	private int initialMouseX, initialMouseY;
	private int initialCategoryX;
	private Feature selectedFeature;
	private ButtonWidget saveButton;
	private final Map<Setting, TextFieldWidget> settingsWidgets = new HashMap<>();

	@Override
	public void removed() {
		persistentCategoryPositions.putAll(categoryPositions);
		persistentCategoryVisibility.putAll(categoryVisibility);
		super.removed();
	}


	public GuiScreen() {
		super(Text.of(""));
		categoryPositions.putAll(persistentCategoryPositions);
		categoryVisibility.putAll(persistentCategoryVisibility);
	}


	@Override
	public void init() {
		searchText = new TextFieldWidget(MC.textRenderer, width / 2 - 75, 25, 150, 20, Text.of(""));
		addSelectableChild(searchText);
		setInitialFocus(searchText);
		ButtonWidget configButton = ButtonWidget.builder(Text.of("Configs..."), b -> MC.setScreen(new ConfigScreen(this))).dimensions(50, height - 25, 100, 20).build();
		ButtonWidget keybindButton = ButtonWidget.builder(Text.of("Keybinds..."), b -> MC.setScreen(new KeybindScreen(this))).dimensions(width - 150, height - 25, 100, 20).build();
		addDrawableChild(keybindButton);
		addDrawableChild(configButton);
	}


	@Override
	public void tick() {
		time += 0.05;
		searchText.tick();
		foundFeatures.clear();
		feature2pos.clear();
		categorizedFeatures.clear();

		FeatureList features = TOASTHACK.getFeatures();
		ArrayList<String> foundFeatureNames = new ArrayList<>();
		Set<String> featureNames = features.getAllFeatureNames();
		featureNames.forEach(e -> {
			if (e.startsWith(searchText.getText()))
				foundFeatureNames.add(e);
		});
		foundFeatureNames.sort(Comparator.naturalOrder());
		foundFeatureNames.forEach(e -> foundFeatures.add(features.getFeature(e)));

		for (Feature feature : foundFeatures) {
			String category = feature.getCategory();
			categorizedFeatures.putIfAbsent(category, new ArrayList<>());
			if (categoryVisibility.getOrDefault(category, true)) {
				categorizedFeatures.get(category).add(feature);
			}
		}

		int defaultStartX = 50;
		int gap = 150;

		for (String category : categorizedFeatures.keySet()) {
			int startX = categoryPositions.getOrDefault(category, defaultStartX);
			int y = 100;

			for (Feature feature : categorizedFeatures.get(category)) {
				feature2pos.put(feature, new int[]{startX, y});
				y += 16;
			}

			defaultStartX += gap;
		}
		super.tick();
		settingsWidgets.values().forEach(TextFieldWidget::tick);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		renderBackground(context);
		super.render(context, mouseX, mouseY, delta);
		searchText.render(context, mouseX, mouseY, delta);

		int defaultStartX = 50;
		int gap = 150;
		int categoryBoxPadding = 5;

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		for (String category : categorizedFeatures.keySet()) {
			int startX = categoryPositions.getOrDefault(category, defaultStartX);
			Color categoryColor = categoryVisibility.getOrDefault(category, true) ? WHITE : GRAY;
			String toggleSymbol = categoryVisibility.getOrDefault(category, true) ? "-" : "+";
			VanillaTextRenderer.INSTANCE.render(toggleSymbol, startX + 85, 75, categoryColor, false);

			VanillaTextRenderer.INSTANCE.render(category, startX, 75, categoryColor, false);

			VanillaTextRenderer.INSTANCE.render("-----------------", startX, 85, categoryColor, false);
			int categoryHeight = categorizedFeatures.get(category).size() * 16 + 16;

			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			bufferBuilder.vertex(startX - categoryBoxPadding, 75 + categoryHeight + categoryBoxPadding, 0).color(0, 0, 0, 90).next();
			bufferBuilder.vertex(startX + 100 + categoryBoxPadding, 75 + categoryHeight + categoryBoxPadding, 0).color(0, 0, 0, 90).next();
			bufferBuilder.vertex(startX + 100 + categoryBoxPadding, 75 - categoryBoxPadding, 0).color(0, 0, 0, 90).next();
			bufferBuilder.vertex(startX - categoryBoxPadding, 75 - categoryBoxPadding, 0).color(0, 0, 0, 90).next();
			tessellator.draw();
			RenderSystem.disableBlend();

			VanillaTextRenderer.INSTANCE.render(category, startX, 75, categoryColor, false);

			if (categoryVisibility.getOrDefault(category, true)) {
				for (Feature feature : categorizedFeatures.get(category)) {
					int[] pos = feature2pos.get(feature);
					if(TOASTHACK.getFeatures().RainbowFeature.isEnabled()) {
						Color color = feature.isEnabled() ? getCyclingColor() : isHoveringOver(mouseX, mouseY, pos[0], pos[1], pos[0] + 100, pos[1] + 8) ? HOVERED : GRAY;
						VanillaTextRenderer.INSTANCE.render(feature.getName(), pos[0], pos[1], color, false);
					} else {
						Color blue = new Color(100,100,255);
						Color color = feature.isEnabled() ? blue : isHoveringOver(mouseX, mouseY, pos[0], pos[1], pos[0] + 100, pos[1] + 8) ? HOVERED : GRAY;
						VanillaTextRenderer.INSTANCE.render(feature.getName(), pos[0], pos[1], color, false);
					}
				}
			}

			defaultStartX += gap;
		}
		if (selectedFeature != null) {
			renderSettingsWidgets(context, mouseX, mouseY, delta);
		}
	}
	private void renderSettingsWidgets(DrawContext context, int mouseX, int mouseY, float delta) {
		if (!settingsWidgets.isEmpty()) {
			int boxPaddingHorizontal = 10;
			int boxPaddingVertical = 10;
			int labelWidth = 100;
			int saveButtonHeight = 20;

			int backgroundX = width - WIDGET_WIDTH - labelWidth - 30 - boxPaddingHorizontal;
			int backgroundY = 40;

			int backgroundHeight = settingsWidgets.size() * (WIDGET_HEIGHT + INTER_WIDGET_PADDING)
					+ boxPaddingVertical * 2 + saveButtonHeight + INTER_WIDGET_PADDING;

			int backgroundWidth = WIDGET_WIDTH + labelWidth + boxPaddingHorizontal * 2;


			drawBackground(backgroundX, backgroundY, backgroundWidth, backgroundHeight);


			int currentY = 50;
			int widgetX = backgroundX + labelWidth + boxPaddingHorizontal;

			for (Map.Entry<Setting, TextFieldWidget> entry : settingsWidgets.entrySet()) {
				Setting setting = entry.getKey();
				TextFieldWidget widget = entry.getValue();

				widget.setX(widgetX);
				widget.setY(currentY);


				Color labelColor = new Color(255, 255, 255);
				VanillaTextRenderer.INSTANCE.render(setting.getName(), backgroundX + boxPaddingHorizontal, currentY, labelColor);


				widget.render(context, mouseX, mouseY, delta);

				currentY += WIDGET_HEIGHT + INTER_WIDGET_PADDING;
			}
		}
	}

	private void drawBackground(int x, int y, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		bufferBuilder.begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(x, y + height, 0).color(0, 0, 0, 90).next();
		bufferBuilder.vertex(x + width, y + height, 0).color(0, 0, 0, 90).next();
		bufferBuilder.vertex(x + width, y, 0).color(0, 0, 0, 90).next();
		bufferBuilder.vertex(x, y, 0).color(0, 0, 0, 90).next();
		tessellator.draw();
		RenderSystem.disableBlend();
	}

	private Color getCyclingColor() {
		int r = (int) ((Math.sin(time) * 0.5 + 0.5) * 255);
		int g = (int) ((Math.sin(time + 2) * 0.5 + 0.5) * 255);
		int b = (int) ((Math.sin(time + 4) * 0.5 + 0.5) * 255);
		return new Color(r, g, b);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		int categoryStartY = 75;
		int categoryEndY = categoryStartY + 16;
		int defaultStartX = 50;
		int gap = 150;

		for (String category : categorizedFeatures.keySet()) {
			int currentXPosition = categoryPositions.getOrDefault(category, defaultStartX);

			if (isHoveringOver((int) mouseX, (int) mouseY, currentXPosition + 85, categoryStartY, currentXPosition + 95, categoryEndY)) {
				categoryVisibility.put(category, !categoryVisibility.getOrDefault(category, true));
				return true;
			}

			if (isHoveringOver((int) mouseX, (int) mouseY, currentXPosition, categoryStartY, currentXPosition + 100, categoryEndY)) {
				currentlyDraggedCategory = category;
				initialMouseX = (int) mouseX;
				initialMouseY = (int) mouseY;
				initialCategoryX = currentXPosition;
				return true;
			}

			defaultStartX += gap;
		}
		Feature clickedFeature = getClickedFeature(mouseX, mouseY);
		if (clickedFeature != null && button == 0) {
			if (clickedFeature.equals(selectedFeature)) {

				selectedFeature = null;
				clearSettingsWidgets();
			} else {

				selectedFeature = clickedFeature;
				setupSettingsWidgets();
			}
			return true;
		}


		currentlyDraggedCategory = null;
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private void saveSettings() {
		for (Map.Entry<Setting, TextFieldWidget> entry : settingsWidgets.entrySet()) {
			Setting setting = entry.getKey();
			TextFieldWidget widget = entry.getValue();
			try {
				setting.loadFromString(widget.getText());

			} catch (Exception e) {

			}
		}
		clearSettingsWidgets();
		selectedFeature = null;
	}


	private void setupSettingsWidgets() {
		settingsWidgets.clear();
		if (selectedFeature != null) {
			int startX = width - WIDGET_WIDTH - 85;
			int startY = 50;

			for (Setting<?> setting : selectedFeature.getSettings()) {
				TextFieldWidget widget = new TextFieldWidget(
						this.textRenderer, startX, startY,
						WIDGET_WIDTH, WIDGET_HEIGHT, Text.of(setting.getName())
				);
				widget.setText(setting.storeAsString());
				settingsWidgets.put(setting, widget);
				this.addDrawableChild(widget);
				startY += WIDGET_HEIGHT + INTER_WIDGET_PADDING;
			}


			saveButton = ButtonWidget.builder(Text.of("Save"), button -> saveSettings())
					.dimensions(startX, startY, WIDGET_WIDTH, 20)
					.build();
			this.addDrawableChild(saveButton);
		}
	}


	private void clearSettingsWidgets() {
		for (TextFieldWidget widget : settingsWidgets.values()) {
			remove(widget);
		}
		settingsWidgets.clear();


		if (saveButton != null) {
			remove(saveButton);
			saveButton = null;
		}
	}


	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (currentlyDraggedCategory != null) {
			int newCategoryX = initialCategoryX + (int) (mouseX - initialMouseX);
			categoryPositions.put(currentlyDraggedCategory, newCategoryX);

			int y = 100;
			for (Feature feature : categorizedFeatures.get(currentlyDraggedCategory)) {
				feature2pos.put(feature, new int[]{newCategoryX, y});
				y += 16;
			}

			return true;
		}
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}


	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		Feature clickedFeature = getClickedFeature(mouseX, mouseY);
		currentlyDraggedCategory = null;

		if (clickedFeature != null) {
			switch (button) {
				case 1:
					clickedFeature.toggle();
					return true;
				default:
					break;
			}
		}

		return super.mouseReleased(mouseX, mouseY, button);
	}

	private Feature getClickedFeature(double mouseX, double mouseY) {
		for (Map.Entry<Feature, int[]> entry : feature2pos.entrySet()) {
			Feature feature = entry.getKey();
			int[] pos = entry.getValue();
			if (isHoveringOver((int) mouseX, (int) mouseY, pos[0], pos[1], pos[0] + 100, pos[1] + 8)) {
				return feature;
			}
		}
		return null;
	}


	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (super.keyPressed(keyCode, scanCode, modifiers)) {
			return true;
		}
		for (TextFieldWidget widget : settingsWidgets.values()) {
			if (widget.keyPressed(keyCode, scanCode, modifiers)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
		return super.keyReleased(keyCode, scanCode, modifiers);
	}

	private boolean isHoveringOver(int mouseX, int mouseY, int x1, int y1, int x2, int y2) {
		return mouseX > Math.min(x1, x2) && mouseX < Math.max(x1, x2) && mouseY > Math.min(y1, y2) && mouseY < Math.max(y1, y2);
	}
}