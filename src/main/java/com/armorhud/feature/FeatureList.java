package com.armorhud.feature;

import com.armorhud.FriendList;
import com.armorhud.features.*;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;

import com.armorhud.setting.Setting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

import static com.armorhud.Client.TOASTHACK;

public class FeatureList
{
	private @Getter @Setter Feature feature;
	private @Getter @Setter Setting setting;
	//blatant
	public final NoJumpDelayFeature noJumpDelayFeature = new NoJumpDelayFeature();
	public final NoFallFeature NoFallFeature = new NoFallFeature();
	//blatant
	public final AimBotFeature aimBotFeature = new AimBotFeature();
	public final FriendList friendList = new FriendList();
	public final TriggerBotFeature triggerBotFeature = new TriggerBotFeature(friendList);
	public final ElytraBoostFeature elytraBoostFeature = new ElytraBoostFeature();
	public final AutoJumpResetFeature autoJumpResetFeature = new AutoJumpResetFeature();
	public final Bow32kFeature bow32kFeature = new Bow32kFeature();
	public final TriggerBotTwoFeature triggerBotTwoFeature = new TriggerBotTwoFeature(friendList);
	public final NoSlowFeature noSlowFeature = new NoSlowFeature();
	public final HUDFeature hUDFeature = new HUDFeature();
	public final FlightFeature flightFeature = new FlightFeature();
	public final GuiMoveFeature guiMoveFeature = new GuiMoveFeature();
	public final LegitAnchorFeature LegitAnchorFeature = new LegitAnchorFeature();
	public final AutoMineCartFeature autoMineCartFeature = new AutoMineCartFeature();
	public final AutoLootFeature autoLootFeature = new AutoLootFeature();
	public final AutoWTapFeature autoWTapFeature = new AutoWTapFeature();
	public final AutoObsidianCrystalFeature AutoObsidianCrystalFeature = new AutoObsidianCrystalFeature();
	public final AntiCrystalBounceFeature antiCrystalBounceFeature = new AntiCrystalBounceFeature();
	public final CameraNoClipFeature cameraNoClipFeature = new CameraNoClipFeature();
	public final LegitCrystalFeature legitCrystalFeature = new LegitCrystalFeature();
	public final FakePlayerFeature fakePlayerFeature = new FakePlayerFeature();
	public final FastAnchorFeature fastAnchorFeature = new FastAnchorFeature();
	public final NoOverlayFeature noOverlayFeature = new NoOverlayFeature();
	public final FullBrightFeature fullBrightFeature = new FullBrightFeature();
	public final FastExpFeature fastExpFeature = new FastExpFeature();
	public final AutoInventoryTotemFeature autoInventoryTotemFeature = new AutoInventoryTotemFeature();
	public final AntiAntiXrayFeature antiAntiXrayFeature = new AntiAntiXrayFeature();
	public final AutoFishFeature autoFishFeature = new AutoFishFeature();
	public final ReachFeature reachFeature = new ReachFeature();
	public final XRayFeature xRayFeature = new XRayFeature();

	public final AutoGappleFeature autoGappleFeature = new AutoGappleFeature();
	public final ShieldDisablerFeature shieldDisablerFeature = new ShieldDisablerFeature(friendList);
	public final BoatExecutorFeature boatExecutorFeature = new BoatExecutorFeature();
	public final AimAssistFeature aimAssistFeature = new AimAssistFeature(friendList);
	public final TrueSightFeature trueSightFeature = new TrueSightFeature();
	public final AutoPotFeature autoPotFeature = new AutoPotFeature();
	public final StorageESPFeature storageESPFeature = new StorageESPFeature();
	public final AutoSprintFeature autoSprintFeature = new AutoSprintFeature();
	public final FreecamFeature freecamFeature = new FreecamFeature();
	public final SpeedBridgeFeature speedBridgeFeature = new SpeedBridgeFeature();
	public final DiscordNotifierFeature discordNotifierFeature = new DiscordNotifierFeature();
	public final AntiDoubleTapFeature antiDoubleTapFeature = new AntiDoubleTapFeature();
	public final ESPFeature espFeature = new ESPFeature();
	public final RainbowFeature RainbowFeature = new RainbowFeature();
	public final AutoInventoryTotemLegitFeature autoInventoryTotemLegitFeature = new AutoInventoryTotemLegitFeature();
	public final VulcanFlyFeature vulcanFlyFeature = new VulcanFlyFeature();

	private HashMap<String, Feature> features = new HashMap<>();

	public FeatureList()
	{
		try
		{
			for (Field field : FeatureList.class.getDeclaredFields())
			{
				if (!field.getName().endsWith("Feature"))
					continue;

				Feature feature = (Feature) field.get(this);
				features.put(feature.getName().toLowerCase(), feature);
			}
		} catch (Exception e)
		{
			String message = "Initializing Client features";
			CrashReport report = CrashReport.create(e, message);
			throw new CrashException(report);
		}

	}


	public void turnOffAll()
	{
		features.forEach((name, feature) -> feature.setEnabled(false));
	}


	public void recievePacket(Packet<?> packet) {
		if (feature.getState()) {
			feature.onReceivePacket(packet);
		}
	}
	public void sendPacket(Packet<?> packet) {
		if (feature.getState()) {
			feature.onSendPacket(packet);
		}
	}

	public Feature getFeature(String name)
	{
		return features.get(name);
	}

	public Set<String> getAllFeatureNames()
	{
		return features.keySet();
	}

	public void loadFromFile(String path) {
		Path configFilePath = Path.of(path);
		try {
			if (Files.notExists(configFilePath)) {
				System.err.println("Config file not found: " + path);
				return;
			}
			Scanner scanner = new Scanner(configFilePath);
			int state = 1;
			Feature readingFeature = null;
			Setting readingSetting = null;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine().toLowerCase();
				if (state == 1) {
					if (line.startsWith("@")) {
						readingFeature = getFeature(line.substring(1));
						if (readingFeature == null) {
							System.err.println("Feature not found for line: " + line + " in file: " + path);
							continue;
						}
						state = 0;
						continue;
					}
					if (readingFeature == null) {
						System.err.println("No feature specified for setting: " + line + " in file: " + path);
						continue;
					}
					readingSetting = readingFeature.getSetting(line);
					if (readingSetting == null) {
						System.err.println("Setting not found: " + line + " for feature: " + readingFeature.getName() + " in file: " + path);
						continue;
					}
					state = 2;
					continue;
				}
				if (state == 0) {
					if (!line.equals("true") && !line.equals("false")) {
						System.err.println("Invalid enabled state: " + line + " for feature: " + readingFeature.getName() + " in file: " + path);
						state = 1;
						continue;
					}
					readingFeature.setEnabled(line.equals("true"));
					state = 1;
					continue;
				}
				try {
					readingSetting.loadFromString(line);
				} catch (Exception e) {
					System.err.println("Failed to load setting from string: " + line + " for setting: " + readingSetting.getName() + ", Error: " + e.getMessage());
				}
				state = 1;
			}
			if (state != 1) {
				System.err.println("Unexpected end of file: " + path);
			}
		} catch (IOException e) {
			System.err.println("Failed to open config file: " + path + ", Error: " + e.getMessage());
		}
	}




	public void saveAsFile(String path)
	{
		Path configDir = TOASTHACK.getCwHackDirectory().resolve("config");
		try
		{
			Files.createDirectories(configDir);
		} catch (IOException e)
		{
			throw new RuntimeException("Failed to create config folder");
		}
		try
		{
			Path configFilePath = configDir.resolve(path);
			File configFile = new File(configFilePath.toString());
			configFile.createNewFile();
		} catch (IOException e)
		{
			throw new RuntimeException("Failed to create config file");
		}
		Path configFilePath = Path.of(path);
		try
		{
			FileWriter fileWriter = new FileWriter(configFilePath.toString());
			Set<String> featureNames = getAllFeatureNames();
			for (String featureName : featureNames)
			{
				Feature feature = getFeature(featureName);
				fileWriter.write("@" + featureName + "\n");
				fileWriter.write(feature.isEnabled() + "\n");
				for (String settingName : feature.getSettingNames())
				{
					Setting setting = feature.getSetting(settingName);
					fileWriter.write(settingName + "\n");
					fileWriter.write(setting.storeAsString() + "\n");
				}
			}
			fileWriter.close();
		} catch (IOException e)
		{
			throw new RuntimeException("Failed to create config file");
		}
	}
}
