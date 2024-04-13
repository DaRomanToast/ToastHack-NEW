package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

public class ListCommand extends Command
{

	public ListCommand()
	{
		super("list", "List all available features", new String[]{});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		for (String feature : Client.TOASTHACK.getFeatures().getAllFeatureNames())
		{
			ChatUtils.info(Client.TOASTHACK.getFeatures().getFeature(feature).getName());
		}
	}
}
