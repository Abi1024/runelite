package net.runelite.client.plugins.hatcounter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("teamCapes")
public interface HatCounterConfig extends Config
{
    @ConfigItem(
            keyName = "minimumHatCount",
            name = "Minimum Hat Count",
            description = "Configures the minimum number of hats which must be present before being displayed.",
            position = 0
    )
    default int getMinimumHatCount()
    {
        return 1;
    }

    @ConfigItem(
            keyName = "ignoreHeadgear",
            name = "Ignore following headgear ",
            description = "Configures the headgear which should not be displayed in the overlay.",
            position = 1
    )
    default String getIgnoredHeadgear()
    {
        return "";
    }

    @ConfigItem(
            keyName = "showOnlyInPVP",
            name = "Show only in PVP ",
            description = "Configures whether overlay should only be displayed in wilderness and PVP/deadman worlds. ",
            position = 2
    )
    default boolean getshowOnlyInPVP()
    {
        return false;
    }
}