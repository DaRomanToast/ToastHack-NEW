package com.armorhud.command.exception;

import com.armorhud.utils.ChatUtils;

public class CommandSyntaxException extends CommandException
{
	public CommandSyntaxException(String message)
	{
		super(message);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Command Syntax Error: " + getMessage());
	}
}