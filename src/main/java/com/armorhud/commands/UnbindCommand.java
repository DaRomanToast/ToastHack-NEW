package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.utils.ChatUtils;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

public class UnbindCommand extends Command
{

	public UnbindCommand()
	{
		super("unbind", "Unbind a keybind", new String[]{"name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandSyntaxException("argument number not matching");

		Client.TOASTHACK.getKeybindManager().removeKeybind(command[0]);

		ChatUtils.info("keybind unbinded");
	}
}
