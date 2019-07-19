package net.runelite.client.plugins.deathtracker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigItem;

public interface DeathTrackerConfig extends Config {
    @ConfigItem(
            keyName = "showOnlyinPvp",
            name = "Show only PvP deaths",
            description = "What type of dart are you using in your toxic blowpipe"
    )
    default boolean blowpipeAmm()
    {
        return true;
    }
}
