package com.armorhud.macro.action;

import com.armorhud.macro.actions.ClickAction;
import com.armorhud.macro.actions.JumpAction;
import com.armorhud.macro.actions.PressKeyAction;
import com.armorhud.macro.actions.SleepAction;

import java.util.HashMap;

public class ActionList
{

	private final HashMap<String, Class<? extends Action>> actions = new HashMap<>();

	public ActionList()
	{
		actions.put("click", ClickAction.class);
		actions.put("jump", JumpAction.class);
		actions.put("press", PressKeyAction.class);
		actions.put("sleep", SleepAction.class);
	}

	public Class<? extends Action> getAction(String name)
	{
		return actions.get(name);
	}
}
