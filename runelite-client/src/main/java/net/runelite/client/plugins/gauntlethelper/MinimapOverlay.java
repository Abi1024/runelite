package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.GameObject;
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
        return null;
    }

    private void renderSupplySpots(Graphics2D graphics){
        if (plugin.getSupplies().fish < config.num_fish()){
            for (GameObject spot: plugin.getFishing_spots().values()){
                renderMinimapSupplySpot(graphics,config.supplySpotsColor(),spot);
            }
        }
        if (plugin.getSupplies().ore < config.num_resources()){
            for (GameObject spot: plugin.getMining_spots().values()){
                renderMinimapSupplySpot(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().bark < config.num_resources()){
            for (GameObject spot: plugin.getBark_spots().values()){
                renderMinimapSupplySpot(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().linum < config.num_resources()){
            for (GameObject spot: plugin.getLinum_spots().values()){
                renderMinimapSupplySpot(graphics, config.supplySpotsColor(), spot);
            }
        }
        if (plugin.getSupplies().herbs < config.num_herbs()){
            for (GameObject spot: plugin.getHerb_spots().values()){
                renderMinimapSupplySpot(graphics, config.supplySpotsColor(), spot);
            }
        }
    }

    private void renderMinimapSupplySpot(Graphics2D graphics, Color color, GameObject object){
        Point minimapLocation = object.getMinimapLocation();
        if (minimapLocation != null)
        {
            OverlayUtil.renderMinimapLocation(graphics, minimapLocation, color);
        }
    }



}
