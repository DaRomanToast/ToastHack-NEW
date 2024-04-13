package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

import java.nio.file.Path;

public class SaveCommand extends Command
{

	public SaveCommand()
	{
		super("save", "save a configuration", new String[]{"config name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");
		writeConfig(command[0]);

		ChatUtils.info("config saved");
	}

	private void writeConfig(String name)
	{
		Path configDir = Client.TOASTHACK.getConfigDirectory();
		Path configFilePath = configDir.resolve(name + ".cw");
		Client.TOASTHACK.getFeatures().saveAsFile(configFilePath.toString());
	}
}
