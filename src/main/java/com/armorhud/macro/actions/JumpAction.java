package com.armorhud.macro.actions;

import com.armorhud.Client;
import com.armorhud.macro.exception.MacroException;
import com.armorhud.macro.exception.MacroSyntaxException;
import com.armorhud.macro.action.Action;

public class JumpAction extends Action
{

	@Override
	public void init(String[] args) throws MacroException
	{
		if (args.length != 0)
			throw new MacroSyntaxException("argument number not matching");
	}

	@Override
	public void run()
	{
		Client.MC.execute(() ->
		{
			if (Client.MC.player != null && Client.MC.player.isOnGround())
				Client.MC.player.jump();
		});
	}
}
