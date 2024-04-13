package com.armorhud.feature;

import com.armorhud.Client;
import com.armorhud.gui.screen.FeatureSettingScreen;
import com.armorhud.utils.NotificationUtils;
import com.armorhud.utils.RenderUtils;
import com.armorhud.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import static com.armorhud.ClientInitializer.mc;

public abstract class Feature
{
	private final HashMap<String, Setting<?>> settings;

	private final String name;

	private int key;
	private RenderUtils renderUtils;

	public void onToggle() {

	}
	private final String category;
	private boolean state;


	public void onReceivePacket(Packet<?> packet) {

	}
	private boolean enabled;
	public boolean getState() {
		return this.state;
	}

	public void onSendPacket(Packet<?> packet) {

	}
	public void onWorldRender(MatrixStack matrices) {
	}

	public void nullCheck() {
		mc = MinecraftClient.getInstance();
		if(mc.world != null && mc.player != null && mc.getNetworkHandler() != null) {
			return;
		}
	}
	public Feature(String name,	String category)
	{
		this.name = name;
		this.category = category;
		settings = new HashMap<>();
	}


	public void setState(boolean state) {
		this.onToggle();
		if(this.state = state) return;
		if (state) {
			this.onEnable();
			this.state = true;
		} else {
			this.onDisable();
			this.state = false;
		}
	}
	public RenderUtils getRenderUtils() {
		return this.renderUtils;
	}

	public boolean isEnabled()
	{
		return enabled;
	}
	public String getCategory() {
		return category;
	}
	public void setEnabled(boolean enabled)
	{
		if (this.enabled == enabled)
			return;

		this.enabled = enabled;

		if (enabled)
		{
			NotificationUtils.notify(name + " has been enabled");
			onEnable();
		}
		else
		{
			NotificationUtils.notify(name + " has been disabled");
			onDisable();
		}
	}
	public void setKey(int key) {
		this.key = key;
	}

	public String getName()
	{
		return name;
	}



	public Setting<?> getSetting(String name)
	{
		return settings.get(name);
	}

	public Set<String> getSettingNames()
	{
		return settings.keySet();
	}

	public Collection<Setting<?>> getSettings()
	{
		return settings.values();
	}

	public void toggle()
	{
		setEnabled(!enabled);
	}

	public FeatureSettingScreen getSettingScreen()
	{
		return new FeatureSettingScreen(Client.MC.currentScreen, this);
	}

	public void addSetting(Setting<?> setting)
	{
		settings.put(setting.getName().toLowerCase(), setting);
	}

	protected void onEnable()
	{

	}

	protected void onDisable()
	{

	}

	public void onChangeSetting(Setting<?> setting)
	{

	}
}