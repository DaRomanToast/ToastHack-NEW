package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.feature.Feature;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandInvalidArgumentException;
import com.armorhud.command.exception.CommandSyntaxException;
import com.armorhud.setting.Setting;

public class ListSettingsCommand extends Command
{

	public ListSettingsCommand()
	{
		super("ListSettings", "List all the settings of a feature", new String[]{"feature name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		Feature feature = Client.TOASTHACK.getFeatures().getFeature(command[0]);
		if (feature == null)
			throw new CommandInvalidArgumentException("no feature named " + command[0]);
		for (Setting setting : feature.getSettings())
		{
			ChatUtils.info(setting.getName() + ": " + setting.storeAsString());
		}
	}
}
