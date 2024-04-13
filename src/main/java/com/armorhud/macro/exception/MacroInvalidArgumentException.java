package com.armorhud.macro.exception;

import com.armorhud.utils.ChatUtils;

public class MacroInvalidArgumentException extends MacroException
{

	public MacroInvalidArgumentException(String message)
	{
		super(message);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Macro Invalid Argument Exception: " + getMessage());
	}
}
