package net.runelite.client.plugins.gauntlethelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

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
            name = "Highlight supply spots",
            description = "If set, this option higlights in game objects that yield supplies you need to collect.",
            position = 3
    )
    default boolean supplySpots()
    {
        return true;
    }

    @ConfigItem(
            keyName = "supplySpotsColor",
            name = "Supply spots color",
            description = "Sets the color of supply spots.",
            position = 4
    )
    default Color supplySpotsColor()
    {
        return Color.GREEN;
    }

    @ConfigItem(
            keyName = "minimapOverlay",
            name = "Minimap overlay",
            description = "Configures whether or not to show the minimap overlay.",
            position = 4
    )
    default boolean minimapOverlay()
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
            keyName = "bossMonsters",
            name = "Higlight boss monsters",
            description = "Highlights the three boss monsters.",
            position = 4
    )
    default boolean bossMonsters()
    {
        return true;
    }

    @ConfigItem(
            keyName = "highlightMonsters",
            name = "Highlight non-boss monsters",
            description = "If there are weapon frames or shards left to be collected, this will higlight non-boss monsters.",
            position = 4
    )
    default boolean highlightMonsters()
    {
        return true;
    }

    @ConfigItem(
            keyName = "monstersColor",
            name = "Highlight monsters color",
            description = "Sets the color with which to higlight monsters.",
            position = 4
    )
    default Color monstersColor()
    {
        return Color.CYAN;
    }

    @ConfigItem(
            keyName = "tornados",
            name = "Higlight tornados",
            description = "Configures whether tornados should be highlighted and how long they last.",
            position = 4
    )
    default boolean showTornados(){ return true;}


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
