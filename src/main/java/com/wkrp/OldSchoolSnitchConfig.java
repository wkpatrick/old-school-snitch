package com.wkrp;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("Old School Snitch")
public interface OldSchoolSnitchConfig extends Config {
    @ConfigItem(keyName = "apiKey", name = "Api Key", description = "API Key for the character to send the data for")
    default String apiKey() {
        return "";
    }

    @ConfigItem(keyName = "enableXpTracking", name = "Enable XP Tracking", description = "Send XP drops to Old School Snitch")
    default boolean xpTrackingCheckbox() {
        return false;
    }

    @ConfigItem(keyName = "enableKillTracking", name = "Enable Kill Tracking", description = "Send NPC Kills to Old School Snitch")
    default boolean killrackingCheckbox() {
        return false;
    }
}
