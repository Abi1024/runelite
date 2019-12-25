/*
 * Copyright (c) 2018, Seth <http://github.com/sethtroll>
 * All rights reserved.
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
package net.runelite.client.plugins.implings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ImplingMinimapOverlay extends Overlay
{
	private final Client client;
	private final ImplingsPlugin plugin;
	private final ImplingsConfig config;

	@Inject
	private ImplingMinimapOverlay(Client client, ImplingsPlugin plugin, ImplingsConfig config)
	{
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Map<Integer, String> dynamicSpawns = plugin.getDynamicSpawns();
		for (Map.Entry<Integer, String> dynamicSpawn : dynamicSpawns.entrySet())
		{
			drawDynamicSpawn(graphics, dynamicSpawn.getKey(), dynamicSpawn.getValue(), config.getDynamicSpawnColor());

		}

		List<NPC> imps = plugin.getImplings();

		if (imps.isEmpty())
		{
			return null;
		}

		for (NPC imp : imps)
		{
			Point impLocation = imp.getMinimapLocation();
			Color color = plugin.npcToColor(imp);
			if (!plugin.showNpc(imp) || impLocation == null || color == null)
			{
				continue;
			}

			OverlayUtil.renderMinimapLocation(graphics, impLocation, color);

			if (config.showName())
			{
				Point textLocation = new Point(impLocation.getX() + 1, impLocation.getY());
				OverlayUtil.renderTextLocation(graphics, textLocation, imp.getName(), color);
			}
		}

		return null;
	}

	private void drawDynamicSpawn(Graphics2D graphics, Integer spawnID, String text, Color color)
	{
		List<NPC> npcs = client.getNpcs();
		for (NPC npc : npcs)
		{
			Point impLocation = npc.getMinimapLocation();
			if (npc.getComposition().getId() != spawnID)
			{
				continue;
			}
			NPCComposition composition = npc.getComposition();
			if (composition.getConfigs() != null)
			{
				NPCComposition transformedComposition = composition.transform();
				if (transformedComposition == null)
				{
					Point textLocation = new Point(impLocation.getX() + 1, impLocation.getY());
					OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
					OverlayUtil.renderMinimapLocation(graphics, impLocation, color);
				}
			}

		}
	}
}
