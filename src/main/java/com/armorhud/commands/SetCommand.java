package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.feature.Feature;
import com.armorhud.feature.FeatureList;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandInvalidArgumentException;
import com.armorhud.command.exception.CommandSyntaxException;
import com.armorhud.setting.Setting;

public class SetCommand extends Command
{
	public SetCommand()
	{
		super("set", "Set a value of a feature", new String[]{"feature", "setting", "value"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length <= 2)
			throw new CommandSyntaxException("arguments number not matching");
		FeatureList featureList = Client.TOASTHACK.getFeatures();
		Feature feature2Set = featureList.getFeature(command[0]);
		if (feature2Set == null)
			throw new CommandInvalidArgumentException("no feature named " + command[0]);

		Setting setting = feature2Set.getSetting(command[1]);
		if (setting == null)
			throw new CommandInvalidArgumentException("no setting named " + command[1]);

		try
		{
			String string;
			string = command[2];
			for (int i = 3; i < command.length; i++)
			{
				string = string + " ";
				string = string + command[i];
			}
			setting.loadFromString(string);
		} catch (Exception e)
		{
			throw new CommandInvalidArgumentException("can't parse the value");
		}

		ChatUtils.info(setting.getName() + " has been set to " + command[2]);
	}
}
