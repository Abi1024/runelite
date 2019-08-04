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
            description = "Configures whether or not to display a chat message whenever the boss switches its prayer.",
            position = 2
    )
    default boolean showPlayerSwitch()
    {
        return true;
    }

    @ConfigItem(
            keyName = "supplySpots",
            name = "Show supply spots",
            description = "If set, this option higlights in game objects that yield supplies you need to collect.",
            position = 3
    )
    default boolean supplySpots()
    {
        return true;
    }

    @ConfigItem(
            keyName = "suppliesInfoBox",
            name = "Supplies needed infobox",
            description = "Displays an infobox showing what supplies still need to be collected.",
            position = 4
    )
    default boolean suppliesInfoBox()
    {
        return true;
    }

    @ConfigItem(
            keyName = "trackHitpoints",
            name = "Track hitpoints",
            description = "If set, this option takes into account your current HP to determine how much more fish you need.",
            position = 4
    )
    default boolean trackHitpoints()
    {
        return true;
    }

    @ConfigItem(
            keyName = "armorResources",
            name = "Armor resources",
            description = "Number of ore, bark, and linum you plan to collect.",
            position = 5
    )
    default int num_resources()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "fish",
            name = "Fish",
            description = "Number of fish you plan to collect.",
            position = 6
    )
    default int num_fish()
    {
        return 26;
    }

    @ConfigItem(
            keyName = "herbs",
            name = "Herbs",
            description = "Number of herbs you plan to collect.",
            position = 7
    )
    default int num_herbs()
    {
        return 2;
    }

    @ConfigItem(
            keyName = "shards",
            name = "Crystal shards",
            description = "Number of shards you plan to collect.",
            position = 8
    )
    default int num_shards()
    {
        return 420;
    }

    @ConfigItem(
            keyName = "weapons",
            name = "Weapon frames",
            description = "Number of weapon frames you plan to collect.",
            position = 9
    )
    default int num_weapons()
    {
        return 2;
    }

}
