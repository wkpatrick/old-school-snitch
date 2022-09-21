package com.wkrp;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OldSchoolSnitchPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OldSchoolSnitchPlugin.class);
		RuneLite.main(args);
	}
}