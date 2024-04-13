package com.armorhud.macro.actions;

import com.armorhud.Client;
import com.armorhud.macro.exception.MacroException;
import com.armorhud.macro.exception.MacroInvalidArgumentException;
import com.armorhud.macro.exception.MacroSyntaxException;
import com.armorhud.mixinterface.IKeyboard;
import com.armorhud.macro.action.Action;
import org.lwjgl.glfw.GLFW;

public class PressKeyAction extends Action
{

	private int key;

	@Override
	public void init(String[] args) throws MacroException
	{
		if (args.length != 1)
			throw new MacroSyntaxException("argument number not matching");
		try
		{
			key = Integer.parseInt(args[0]);
		} catch (Exception e)
		{
			throw new MacroInvalidArgumentException("can't parse the value");
		}
	}

	@Override
	public void run()
	{
		Client.MC.execute(this::runInner);
	}

	private void runInner()
	{
		IKeyboard iKeyboard = (IKeyboard) Client.MC.keyboard;
		long window = Client.MC.getWindow().getHandle();
		Client.MC.keyboard.onKey(window, key, 0, GLFW.GLFW_PRESS, 0);
		Client.MC.keyboard.onKey(window, key, 0, GLFW.GLFW_RELEASE, 0);
		iKeyboard.cwOnChar(window, key, 0);
	}

}
