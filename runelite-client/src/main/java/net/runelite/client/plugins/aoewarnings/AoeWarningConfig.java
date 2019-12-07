/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Modified by farhan1666
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.aoewarnings;

import java.awt.Color;
import java.awt.Font;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("aoe")
public interface AoeWarningConfig extends Config
{
	@Getter
	@AllArgsConstructor
	enum FontStyle
	{
		BOLD("Bold", Font.BOLD),
		ITALIC("Italic", Font.ITALIC),
		PLAIN("Plain", Font.PLAIN);

		private String name;
		private int font;

		@Override
		public String toString()
		{
			return getName();
		}
	}

	@ConfigItem(
		keyName = "aoeNotifyAll",
		name = "Notify for all AoE warnings",
		description = "Configures whether or not AoE Projectile Warnings should trigger a notification",
		position = 0
	)
	default boolean aoeNotifyAll()
	{
		return false;
	}


	@ConfigItem(
		position = 2,
		keyName = "overlayColor",
		name = "Overlay Color",
		description = "Configures the color of the AoE Projectile Warnings overlay"
	)
	default Color overlayColor()
	{
		return new Color(0, 150, 200);
	}

	@ConfigItem(
		keyName = "outline",
		name = "Display Outline",
		description = "Configures whether or not AoE Projectile Warnings have an outline",
		position = 3
	)
	default boolean isOutlineEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "delay",
		name = "Fade Delay",
		description = "Configures the amount of time in milliseconds that the warning lingers for after the projectile has touched the ground",
		position = 4
	)
	default int delay()
	{
		return 300;
	}

	@ConfigItem(
		keyName = "fade",
		name = "Fade Warnings",
		description = "Configures whether or not AoE Projectile Warnings fade over time",
		position = 5
	)
	default boolean isFadeEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tickTimers",
		name = "Tick Timers",
		description = "Configures whether or not AoE Projectile Warnings has tick timers overlaid as well.",
		position = 6
	)
	default boolean tickTimers()
	{
		return true;
	}

	@ConfigItem(
		position = 8,
		keyName = "fontStyle",
		name = "Font Style",
		description = "Bold/Italics/Plain",
		hidden = true
	)
	default FontStyle fontStyle()
	{
		return FontStyle.BOLD;
	}

	@Range(
		min = 20,
		max = 40
	)
	@ConfigItem(
		position = 9,
		keyName = "textSize",
		name = "Text Size",
		description = "Text Size for Timers.",
		hidden = true
	)
	default int textSize()
	{
		return 32;
	}

	@ConfigItem(
		position = 10,
		keyName = "shadows",
		name = "Shadows",
		description = "Adds Shadows to text.",
		hidden = true
	)
	default boolean shadows()
	{
		return true;
	}

	@ConfigItem(
		keyName = "lizardmanaoe",
		name = "Lizardman Shamans",
		description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans is displayed",
		position = 13
	)
	default boolean isShamansEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "lizardmanaoenotify",
		name = "Lizardman Shamans Notify",
		description = "Configures whether or not AoE Projectile Warnings for Lizardman Shamans should trigger a notification",
		position = 14
	)
	default boolean isShamansNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "archaeologistaoe",
		name = "Crazy Archaeologist",
		description = "Configures whether or not AoE Projectile Warnings for Archaeologist is displayed",
		position = 16
	)
	default boolean isArchaeologistEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "archaeologistaoenotify",
		name = "Crazy Archaeologist Notify",
		description = "Configures whether or not AoE Projectile Warnings for Crazy Archaeologist should trigger a notification",
		position = 17
	)
	default boolean isArchaeologistNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "icedemon",
		name = "Ice Demon",
		description = "Configures whether or not AoE Projectile Warnings for Ice Demon is displayed",
		position = 19
	)
	default boolean isIceDemonEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "icedemonnotify",
		name = "Ice Demon Notify",
		description = "Configures whether or not AoE Projectile Warnings for Ice Demon should trigger a notification",
		position = 20
	)
	default boolean isIceDemonNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "vasa",
		name = "Vasa",
		description = "Configures whether or not AoE Projectile Warnings for Vasa is displayed",
		position = 22
	)
	default boolean isVasaEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vasanotify",
		name = "Vasa Notify",
		description = "Configures whether or not AoE Projectile Warnings for Vasa should trigger a notification",
		position = 23
	)
	default boolean isVasaNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "tekton",
		name = "Tekton",
		description = "Configures whether or not AoE Projectile Warnings for Tekton is displayed",
		position = 25
	)
	default boolean isTektonEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "tektonnotify",
		name = "Tekton Notify",
		description = "Configures whether or not AoE Projectile Warnings for Tekton should trigger a notification",
		position = 26
	)
	default boolean isTektonNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "vorkath",
		name = "Vorkath",
		description = "Configures whether or not AoE Projectile Warnings for Vorkath are displayed",
		position = 28
	)
	default boolean isVorkathEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vorkathotify",
		name = "Vorkath Notify",
		description = "Configures whether or not AoE Projectile Warnings for Vorkath should trigger a notification",
		position = 29
	)
	default boolean isVorkathNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "galvek",
		name = "Galvek",
		description = "Configures whether or not AoE Projectile Warnings for Galvek are displayed",
		position = 31
	)
	default boolean isGalvekEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "galveknotify",
		name = "Galvek Notify",
		description = "Configures whether or not AoE Projectile Warnings for Galvek should trigger a notification",
		position = 32
	)
	default boolean isGalvekNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "gargboss",
		name = "Gargoyle Boss",
		description = "Configs whether or not AoE Projectile Warnings for Dawn/Dusk are displayed",
		position = 34
	)
	default boolean isGargBossEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "gargbossnotify",
		name = "Gargoyle Boss Notify",
		description = "Configures whether or not AoE Projectile Warnings for Gargoyle Bosses should trigger a notification",
		position = 35
	)
	default boolean isGargBossNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "vetion",
		name = "Vet'ion",
		description = "Configures whether or not AoE Projectile Warnings for Vet'ion are displayed",
		position = 37
	)
	default boolean isVetionEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vetionnotify",
		name = "Vet'ion Notify",
		description = "Configures whether or not AoE Projectile Warnings for Vet'ion should trigger a notification",
		position = 38
	)
	default boolean isVetionNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "chaosfanatic",
		name = "Chaos Fanatic",
		description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic are displayed",
		position = 40
	)
	default boolean isChaosFanaticEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chaosfanaticnotify",
		name = "Chaos Fanatic Notify",
		description = "Configures whether or not AoE Projectile Warnings for Chaos Fanatic should trigger a notification",
		position = 41
	)
	default boolean isChaosFanaticNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "olm",
		name = "Olm",
		description = "Configures whether or not AoE Projectile Warnings for The Great Olm are displayed",
		position = 43
	)
	default boolean isOlmEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "olmnotify",
		name = "Olm Notify",
		description = "Configures whether or not AoE Projectile Warnings for Olm should trigger a notification",
		position = 44
	)
	default boolean isOlmNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "bombDisplay",
		name = "Olm Bombs",
		description = "Display a timer and colour-coded AoE for Olm's crystal-phase bombs.",
		position = 46
	)
	default boolean bombDisplay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "bombDisplaynotify",
		name = "Olm Bombs Notify",
		description = "Configures whether or not AoE Projectile Warnings for Olm Bombs should trigger a notification",
		position = 47
	)
	default boolean bombDisplayNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "lightning",
		name = "Olm Lightning Trails",
		description = "Show Lightning Trails",
		position = 49
	)
	default boolean LightningTrail()
	{
		return true;
	}

	@ConfigItem(
		keyName = "lightningnotify",
		name = "Olm Lightning Trails Notify",
		description = "Configures whether or not AoE Projectile Warnings for Olm Lightning Trails should trigger a notification",
		position = 50
	)
	default boolean LightningTrailNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "corp",
		name = "Corporeal Beast",
		description = "Configures whether or not AoE Projectile Warnings for the Corporeal Beast are displayed",
		position = 52
	)
	default boolean isCorpEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "corpnotify",
		name = "Corporeal Beast Notify",
		description = "Configures whether or not AoE Projectile Warnings for Corporeal Beast should trigger a notification",
		position = 53
	)
	default boolean isCorpNotifyEnabled()
	{
		return false;
	}


	@ConfigItem(
		keyName = "wintertodt",
		name = "Wintertodt Snow Fall",
		description = "Configures whether or not AOE Projectile Warnings for the Wintertodt snow fall are displayed",
		position = 55
	)
	default boolean isWintertodtEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "wintertodtnotify",
		name = "Wintertodt Snow Fall Notify",
		description = "Configures whether or not AoE Projectile Warnings for Wintertodt Snow Fall Notify should trigger a notification",
		position = 56
	)
	default boolean isWintertodtNotifyEnabled()
	{
		return false;
	}


	@ConfigItem(
		keyName = "isXarpusEnabled",
		name = "Xarpus",
		description = "Configures whether or not AOE Projectile Warnings for Xarpus are displayed",
		position = 58
	)
	default boolean isXarpusEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "isXarpusEnablednotify",
		name = "Xarpus Notify",
		description = "Configures whether or not AoE Projectile Warnings for Xarpus should trigger a notification",
		position = 59
	)
	default boolean isXarpusNotifyEnabled()
	{
		return false;
	}


	@ConfigItem(
		keyName = "addyDrags",
		name = "Addy Drags",
		description = "Show Bad Areas",
		position = 61
	)
	default boolean addyDrags()
	{
		return true;
	}

	@ConfigItem(
		keyName = "addyDragsnotify",
		name = "Addy Drags Notify",
		description = "Configures whether or not AoE Projectile Warnings for Addy Dragons should trigger a notification",
		position = 62
	)
	default boolean addyDragsNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "drake",
		name = "Drakes Breath",
		description = "Configures if Drakes Breath tile markers are displayed",
		position = 64
	)
	default boolean isDrakeEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "drakenotify",
		name = "Drakes Breath Notify",
		description = "Configures whether or not AoE Projectile Warnings for Drakes Breath should trigger a notification",
		position = 65
	)
	default boolean isDrakeNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "cerbFire",
		name = "Cerberus Fire",
		description = "Configures if Cerberus fire tile markers are displayed",
		position = 67
	)
	default boolean isCerbFireEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "cerbFirenotify",
		name = "Cerberus Fire Notify",
		description = "Configures whether or not AoE Projectile Warnings for Cerberus his fire should trigger a notification",
		position = 68
	)
	default boolean isCerbFireNotifyEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "demonicGorilla",
		name = "Demonic Gorilla",
		description = "Configures if Demonic Gorilla boulder tile markers are displayed",
		position = 70
	)
	default boolean isDemonicGorillaEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "demonicGorillaNotify",
		name = "Demonic Gorilla Notify",
		description = "Configures whether or not AoE Projectile Warnings for Demonic Gorilla boulders should trigger a notification",
		position = 71
	)
	default boolean isDemonicGorillaNotifyEnabled()
	{
		return false;
	}
}