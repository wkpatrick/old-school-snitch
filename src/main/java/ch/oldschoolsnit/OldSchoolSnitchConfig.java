package ch.oldschoolsnit;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Old School Snitch")
public interface OldSchoolSnitchConfig extends Config
{
	@ConfigItem(keyName = "apiKey", name = "Api Key", description = "API Key for the character to send the data for", secret = true)
	default String apiKey()
	{
		return "";
	}

	@ConfigItem(keyName = "enableLocationTracking", name = "Enable Location Tracking", description = "Send your in-game location to Old School Snitch")
	default boolean locationTrackingCheckbox()
	{
		return false;
	}

	@ConfigItem(keyName = "enableKillTracking", name = "Enable Kill Tracking", description = "Send NPC Kills to Old School Snitch")
	default boolean killTrackingCheckbox()
	{
		return false;
	}

	@ConfigItem(keyName = "enableDebugMessages", name = "Enable Debug Log Messages", description = "Enable Debug Log Messages")
	default boolean debugMessagesCheckbox()
	{
		return false;
	}
}
