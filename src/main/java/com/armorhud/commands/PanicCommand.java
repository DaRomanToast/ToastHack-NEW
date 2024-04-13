package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

public class PanicCommand extends Command
{
	public PanicCommand()
	{
		super("panic", "Turn off all features and unbind all keybinds", new String[]{});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		Client.TOASTHACK.getKeybindManager().removeAll();
		Client.TOASTHACK.getFeatures().turnOffAll();
	}
}
