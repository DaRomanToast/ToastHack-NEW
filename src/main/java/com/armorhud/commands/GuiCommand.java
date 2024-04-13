package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.gui.screen.GuiScreen;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandSyntaxException;

public class GuiCommand extends Command
{

	public GuiCommand()
	{
		super("gui", "open up gui", new String[] {});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 0)
			throw new CommandSyntaxException("argument number not matching");
		Client.MC.setScreen(new GuiScreen());
	}
}
