package com.armorhud.macro.exception;

import com.armorhud.utils.ChatUtils;

public class MacroSyntaxException extends MacroException
{

	public MacroSyntaxException(String message)
	{
		super(message);
	}

	@Override
	public void printToChat()
	{
		ChatUtils.error("Macro Syntax Error: " + getMessage());
	}
}
