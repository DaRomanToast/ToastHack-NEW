package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.feature.Feature;
import com.armorhud.feature.FeatureList;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandInvalidArgumentException;
import com.armorhud.command.exception.CommandSyntaxException;

public class ToggleCommand extends Command
{
	public ToggleCommand()
	{
		super("toggle", "Toggle a feature", new String[]{"feature"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		FeatureList featureList = Client.TOASTHACK.getFeatures();
		Feature feature2Toggle = featureList.getFeature(command[0]);
		if (feature2Toggle == null)
			throw new CommandInvalidArgumentException("no feature named " + command[0]);
		feature2Toggle.toggle();
	}
}
