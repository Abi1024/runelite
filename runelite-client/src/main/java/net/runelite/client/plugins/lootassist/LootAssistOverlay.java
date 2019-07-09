package net.runelite.client.plugins.lootassist;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class LootAssistOverlay extends Overlay
{
	@Inject
	private Client client;

    private final LootAssistPlugin plugin;
    private final LootAssitConfig config;
	private DecimalFormat d = new DecimalFormat("##.#");

	@Inject
	public LootAssistOverlay(LootAssistPlugin plugin, Client client, LootAssitConfig config)
	{
        super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
        if (config.getshowOnlyInPVP() && !plugin.isShouldDisplayOverlay()){
            return null;
        }
		for (Map.Entry<WorldPoint, LootPile> entry : LootAssistPlugin.lootPiles.entrySet())
		{
			WorldPoint localPoint = entry.getKey();
			LootPile pile = entry.getValue();
			int x;
			int y;
			try
			{
				x = LocalPoint.fromWorld(client, pile.getLocation()).getSceneX();
				y = LocalPoint.fromWorld(client, pile.getLocation()).getSceneY();
			}
			catch (NullPointerException e)
			{
				continue;
			}
			if (!localPoint.isInScene(client))
			{
				continue;
			}

			long timeRemaining = pile.getTimeAppearing() - System.currentTimeMillis();

			if (timeRemaining < 0)
			{
				LootAssistPlugin.lootPiles.remove(localPoint);
				client.clearHintArrow();
			}
			else
			{
				String nameOverlay = pile.getPlayerName();
				String timeOverlay = d.format((pile.getTimeAppearing() - System.currentTimeMillis()) / 1000f);
				final Polygon poly = Perspective.getCanvasTilePoly(client,
					client.getScene().getTiles()[client.getPlane()][x][y].getLocalLocation());
				float ratio = (float)(timeRemaining)/LootPile.getTIME_UNTIL_VISIBLE();
				Color color = new Color(Color.HSBtoRGB(ratio*.33F,1F,1F));
				if (poly != null) {
                    Point textLoc = Perspective.getCanvasTextLocation(client, graphics,
                            LocalPoint.fromWorld(client, pile.getLocation()),
                            nameOverlay, graphics.getFontMetrics().getHeight() * 7);
                    Point timeLoc = Perspective.getCanvasTextLocation(client, graphics,
                            LocalPoint.fromWorld(client, pile.getLocation()),
                            timeOverlay, graphics.getFontMetrics().getHeight());
                    OverlayUtil.renderPolygon(graphics, poly, color);
                    OverlayUtil.renderTextLocation(graphics, timeLoc, timeOverlay, config.color());
                    if (textLoc == null){
                        continue;
                    }
                    OverlayUtil.renderTextLocation(graphics, textLoc, nameOverlay,  config.color());
                    if (timeRemaining < 6000) {
                        client.setHintArrow(WorldPoint.fromLocal(client,
                                LocalPoint.fromWorld(client, pile.getLocation())));
                    }
                }
			}
		}
		return null;
	}
}
