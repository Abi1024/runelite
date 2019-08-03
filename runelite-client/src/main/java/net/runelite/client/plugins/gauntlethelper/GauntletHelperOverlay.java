package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;
import java.util.Optional;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class GauntletHelperOverlay extends Overlay {
    private final GauntletHelperConfig config;
    private final GauntletHelperPlugin plugin;

    @Inject
    private Client client;

    @Inject
    GauntletHelperOverlay(Client client, GauntletHelperConfig config, GauntletHelperPlugin plugin)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.showBossStyle()){
            return null;
        }
        Color color = null;
        if (plugin.is_boss_using_range){
            color = new Color(0,255,0);
        }else {
            color = new Color(255, 0, 0);
        }
        if (!client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().isPresent()){
            return null;
        }
        NPC hunllef = client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().get();
        renderBoss(graphics,color,hunllef);
        return null;
    }

    public void renderBoss(Graphics2D graphics, Color color, NPC npc){
        Polygon objectClickbox = npc.getConvexHull();
        if (objectClickbox != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(objectClickbox);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(objectClickbox);
        }
    }
}
