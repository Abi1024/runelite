package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class GauntletHelperOverlay extends Overlay {
    private final GauntletHelperConfig config;
    private final GauntletHelperPlugin plugin;

    private final ItemManager manager;

    @Inject
    private Client client;

    @Inject
    GauntletHelperOverlay(Client client, GauntletHelperConfig config, GauntletHelperPlugin plugin, ItemManager manager)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.manager = manager;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInGauntlet()){
            return null;
        }
        if (config.showBossStyle()){
            renderBoss(graphics);
        }
        if (config.supplySpots()){
            renderSupplySpots(graphics);
        }
        if (config.bossMonsters()){
            renderMinibosses(graphics);
        }
        return null;
    }

    private void renderBoss(Graphics2D graphics){
        Color color = null;
        if (plugin.is_boss_using_range){
            color = new Color(0,255,0);
        }else {
            color = new Color(255, 0, 0);
        }
        if (!client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().isPresent()){
            return;
        }
        NPC hunllef = client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().get();
        renderActor(graphics,color,hunllef);
    }

    private void renderSupplySpots(Graphics2D graphics){
        if (plugin.getSupplies().fish < config.num_fish()){
            for (GameObject spot: plugin.getFishing_spots().values()){
                renderObject(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().ore < config.num_resources()){
            for (GameObject spot: plugin.getMining_spots().values()){
                renderObject(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().bark < config.num_resources()){
            for (GameObject spot: plugin.getBark_spots().values()){
                renderObject(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().linum < config.num_resources()){
            for (GameObject spot: plugin.getLinum_spots().values()){
                renderObject(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().herbs < config.num_herbs()){
            for (GameObject spot: plugin.getHerb_spots().values()){
                renderObject(graphics, config.supplySpotsColor(), spot);
            }
        }
    }

    private void renderMinibosses(Graphics2D graphics){
        for (NPC npc : plugin.getBosses()){
            if (npc.getName().toLowerCase().contains("beast")){
                renderActor(graphics, new Color(0,100,0), npc);
            }else if (npc.getName().toLowerCase().contains("dragon")){
                renderActor(graphics, Color.RED, npc);
            }if (npc.getName().toLowerCase().contains("bear")){
                renderActor(graphics, Color.BLUE, npc);
            }
        }
    }

    private void renderObject(Graphics2D graphics, Color color, GameObject object){
        Polygon objectClickbox = object.getConvexHull();
        drawPolygon(graphics, objectClickbox, color);
    }


    private void renderActor(Graphics2D graphics, Color color, Actor actor){
        Polygon objectClickbox = actor.getConvexHull();
        drawPolygon(graphics, objectClickbox, color);
    }

    private void drawPolygon(Graphics2D graphics, Polygon polygon, Color color){
        if (polygon != null)
        {
            graphics.setColor(color);
            graphics.setStroke(new BasicStroke(2));
            graphics.draw(polygon);
            graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
            graphics.fill(polygon);
        }
    }

}
