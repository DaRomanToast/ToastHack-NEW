package com.armorhud.commands;

import com.armorhud.Client;
import com.armorhud.command.Command;
import com.armorhud.command.exception.CommandException;
import com.armorhud.command.exception.CommandInvalidArgumentException;
import com.armorhud.command.exception.CommandMacroException;

public class LoadMacroCommand extends Command
{

	public LoadMacroCommand()
	{
		super("loadmacro", "load and compile a macro", new String[]{"macro name"});
	}

	@Override
	public void execute(String[] command) throws CommandException
	{
		if (command.length != 1)
			throw new CommandInvalidArgumentException("argument number not matching");
		try
		{
			Client.TOASTHACK.getMacroManager().loadMacro(command[0]);
		} catch (Exception e)
		{
			throw new CommandMacroException("failed to load the macro");
		}
	}
}
