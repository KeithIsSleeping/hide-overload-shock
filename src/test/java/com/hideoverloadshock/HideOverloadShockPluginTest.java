package com.hideoverloadshock;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class HideOverloadShockPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(HideOverloadShockPlugin.class);
		RuneLite.main(args);
	}
}
