package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

import javax.inject.Inject;
import java.awt.*;

public class MinimapOverlay extends Overlay {
    private final GauntletHelperConfig config;
    private final GauntletHelperPlugin plugin;

    @Inject
    MinimapOverlay(GauntletHelperConfig config, GauntletHelperPlugin plugin)
    {
        this.config = config;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInGauntlet()){
            return null;
        }
        if (!config.minimapOverlay()){
            return null;
        }
        if (config.supplySpots()){
            renderSupplySpots(graphics);
        }
        if (config.bossMonsters()){
            renderMinibosses(graphics);
        }
        if (config.highlightMonsters()){
            renderMonsters(graphics);
        }
        return null;
    }

    private void renderSupplySpots(Graphics2D graphics){
        for (GameObject spot : plugin.getResource_spots().values()){
            switch (spot.getId()){
                case ObjectID.FISHING_SPOT_36068:
                case ObjectID.FISHING_SPOT_35971:
                    if (plugin.getSupplies().fish < config.num_fish()){
                        renderMinimapObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.CRYSTAL_DEPOSIT:
                case ObjectID.CORRUPT_DEPOSIT:
                    if (plugin.getSupplies().ore < config.num_resources()){
                        renderMinimapObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.LINUM_TIRINUM:
                case ObjectID.LINUM_TIRINUM_36072:
                    if (plugin.getSupplies().linum < config.num_resources()){
                        renderMinimapObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.PHREN_ROOTS:
                case ObjectID.PHREN_ROOTS_36066:
                    if (plugin.getSupplies().bark < config.num_resources()){
                        renderMinimapObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.GRYM_ROOT:
                case ObjectID.GRYM_ROOT_36070:
                    if (plugin.getSupplies().herbs < config.num_herbs()){
                        renderMinimapObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void renderMinibosses(Graphics2D graphics){
        for (NPC npc : plugin.getBosses()){
            if (npc.getName().toLowerCase().contains("beast")){
                renderMinimapActor(graphics,  new Color(0,100,0), npc);
            }else if (npc.getName().toLowerCase().contains("dragon")){
                renderMinimapActor(graphics,  Color.RED, npc);
            }if (npc.getName().toLowerCase().contains("bear")){
                renderMinimapActor(graphics,  Color.BLUE, npc);
            }
        }
    }

    private void renderMonsters(Graphics2D graphics){
        for (NPC npc : plugin.getMonsters()){
            renderMinimapActor(graphics, config.monstersColor(), npc);
        }
    }

    private void renderMinimapObject(Graphics2D graphics, Color color, GameObject object){
        Point minimapLocation = object.getMinimapLocation();
        if (minimapLocation != null)
        {
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
        }
    }

    private void renderMinimapActor(Graphics2D graphics, Color color, Actor actor){
        Point minimapLocation = actor.getMinimapLocation();
        if (minimapLocation != null)
        {
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
        }
    }





}
