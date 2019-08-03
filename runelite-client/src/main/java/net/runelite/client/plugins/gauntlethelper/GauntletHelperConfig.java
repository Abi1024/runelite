package net.runelite.client.plugins.gauntlethelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("gauntlethelper")
public interface GauntletHelperConfig extends Config {
    @ConfigItem(
            keyName = "showBossAttackStyle",
            name = "Show boss combat style",
            description = "Configures whether the combat style of the next boss attack should be shown.",
            position = 1
    )
    default boolean showBossStyle()
    {
        return true;
    }

    @ConfigItem(
            keyName = "showPrayerSwitch",
            name = "Show prayer switch",
            description = "Configures whether or not to display a chat message whenever the boss its prayer.",
            position = 2
    )
    default boolean showPlayerSwitch()
    {
        return true;
    }

    @ConfigItem(
            keyName = "trackSupplies",
            name = "Track supplies",
            description = "If set, this option higlights monsters, objects, and items based on the supplies collected so far.",
            position = 3
    )
    default boolean trackSupplies()
    {
        return true;
    }

}
