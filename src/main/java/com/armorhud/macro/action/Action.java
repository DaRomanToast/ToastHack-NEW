package com.armorhud.macro.action;

import com.armorhud.macro.exception.MacroException;

public abstract class Action
{
	public abstract void init(String[] args) throws MacroException;

	public abstract void run();
}
