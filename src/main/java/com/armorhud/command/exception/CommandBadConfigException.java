package com.armorhud.command.exception;

import com.armorhud.utils.ChatUtils;

public class CommandBadConfigException extends CommandException
{
	public CommandBadConfigException(String configName)
	{
		super(configName);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Command Bad Config Error: Config " + getMessage() + " may have corrupted / not found / outdated");
	}
}
