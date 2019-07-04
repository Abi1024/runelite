package net.runelite.client.plugins.lootassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("lootassist")
public interface LootAssitConfig extends Config
{
	@ConfigItem(
		keyName = "color",
		name = "Color",
		description = "The Color of the tile and name overlay that indicates where loot will appear"
	)
	default Color color()
	{
		return Color.WHITE;
	}

	@ConfigItem(
			keyName = "showOnlyInPVP",
			name = "Show only in PVP ",
			description = "Configures whether overlay should only be displayed in wilderness and PVP/deadman worlds. ",
			position = 1
	)
	default boolean getshowOnlyInPVP()
	{
		return false;
	}
}
