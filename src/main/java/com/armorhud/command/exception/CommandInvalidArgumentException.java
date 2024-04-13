package com.armorhud.command.exception;

import com.armorhud.utils.ChatUtils;

public class CommandInvalidArgumentException extends CommandException
{
	public CommandInvalidArgumentException(String message)
	{
		super(message);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Command Invalid Argument Error: " + getMessage());
	}
}
