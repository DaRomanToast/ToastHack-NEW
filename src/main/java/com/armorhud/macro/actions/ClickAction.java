package com.armorhud.macro.actions;

import com.armorhud.Client;
import com.armorhud.macro.exception.MacroException;
import com.armorhud.macro.exception.MacroInvalidArgumentException;
import com.armorhud.macro.exception.MacroSyntaxException;
import com.armorhud.mixinterface.IMouse;
import com.armorhud.macro.action.Action;
import org.lwjgl.glfw.GLFW;

public class ClickAction extends Action
{

	private int button;

	@Override
	public void init(String[] args) throws MacroException
	{
		if (args.length != 1)
			throw new MacroSyntaxException("argument number not matching");
		try
		{
			button = Integer.parseInt(args[0]);
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
		IMouse iMouse = (IMouse) Client.MC.mouse;
		iMouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), button, GLFW.GLFW_PRESS, 0);
		iMouse.cwOnMouseButton(Client.MC.getWindow().getHandle(), button, GLFW.GLFW_RELEASE, 0);
	}
}
