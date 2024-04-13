package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandBadConfigException;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

import java.nio.file.Path;

public class LoadCommand extends Command
{

	public LoadCommand()
	{
		super("load", "load a configuration", new String[]{"config name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		Path configDir = Client.TOASTHACK.getCwHackDirectory().resolve("config");
		Path configFilePath = configDir.resolve(command[0] + ".cw");

		try
		{
			Client.TOASTHACK.getFeatures().loadFromFile(configFilePath.toString());
		}
		catch (Exception e)
		{
			throw new CommandBadConfigException(command[0]);
		}

		ChatUtils.info("config loaded");
	}
}
