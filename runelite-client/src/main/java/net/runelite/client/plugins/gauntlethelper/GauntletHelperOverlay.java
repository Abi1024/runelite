package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;

import javax.inject.Inject;
import java.awt.*;

import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

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
        if (config.highlightMonsters()){
            renderMonsters(graphics);
        }
        if (config.showTornados()){
            renderTornados(graphics);
        }
        if (config.showPlayerSwitch()){
            renderPlayerAttacks(graphics);
        }
        return null;
    }

    private void renderBoss(Graphics2D graphics){
        Color color = null;
        if (plugin.is_boss_using_range()){
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
        for (GameObject spot : plugin.getResource_spots().values()){
            switch (spot.getId()){
                case ObjectID.FISHING_SPOT_36068:
                case ObjectID.FISHING_SPOT_35971:
                    if (plugin.getSupplies().fish < config.num_fish()){
                        renderObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.CRYSTAL_DEPOSIT:
                case ObjectID.CORRUPT_DEPOSIT:
                    if (plugin.getSupplies().ore < config.num_resources()){
                        renderObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.LINUM_TIRINUM:
                case ObjectID.LINUM_TIRINUM_36072:
                    if (plugin.getSupplies().linum < config.num_resources()){
                        renderObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.PHREN_ROOTS:
                case ObjectID.PHREN_ROOTS_36066:
                    if (plugin.getSupplies().bark < config.num_resources()){
                        renderObject(graphics, config.supplySpotsColor(), spot);
                    }
                    break;
                case ObjectID.GRYM_ROOT:
                case ObjectID.GRYM_ROOT_36070:
                    if (plugin.getSupplies().herbs < config.num_herbs()){
                        renderObject(graphics, config.supplySpotsColor(), spot);
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
                renderActor(graphics, new Color(0,100,0), npc);
            }else if (npc.getName().toLowerCase().contains("dragon")){
                renderActor(graphics, Color.RED, npc);
            }if (npc.getName().toLowerCase().contains("bear")){
                renderActor(graphics, Color.BLUE, npc);
            }
        }
    }

    private void renderMonsters(Graphics2D graphics){
        for (NPC npc : plugin.getMonsters()){
            renderActor(graphics, config.monstersColor(), npc);
        }
    }

    private void renderTornados(Graphics2D graphics){
        for (Tornado tornado : plugin.getTornados()){
            if (tornado.getTimeLeft() <= 0)
            {
                return;
            }
            final String textOverlay = Integer.toString(tornado.getTimeLeft());
            final Point textLoc = Perspective.getCanvasTextLocation(client, graphics, tornado.getNpc().getLocalLocation(), textOverlay, 0);
            final LocalPoint lp = LocalPoint.fromWorld(client, tornado.getNpc().getWorldLocation());
            if (lp == null)
            {
                return;
            }

            final Polygon tilePoly = Perspective.getCanvasTilePoly(client, lp);
            OverlayUtil.renderPolygon(graphics, tilePoly, Color.YELLOW);

            if (textLoc == null)
            {
                return;
            }

            Font oldFont = graphics.getFont();
            graphics.setFont(new Font("Courier", Font.BOLD, 14));
            Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);
            OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
            OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.YELLOW);
            graphics.setFont(oldFont);

        }
    }

    private void renderPlayerAttacks(Graphics2D graphics){
        if (!client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().isPresent()){
            return;
        }
        NPC hunllef = client.getNpcs().stream().filter(npc -> (npc.getName() != null) && (npc.getName().contains("Hunllef"))).findFirst().get();
        String textOverlay;
        textOverlay = Integer.toString(4-plugin.getNum_boss_hits());
        if (textOverlay.length() > 0)
        {
            textOverlay += " | ";
        }
        textOverlay += Integer.toString(6-plugin.getNum_player_hits());

        if (textOverlay.length() > 0)
        {
            Point textLoc = Perspective.getCanvasTextLocation(client, graphics, hunllef.getLocalLocation(), textOverlay, hunllef.getLogicalHeight() / 2);

            if (textLoc == null)
            {
                return;
            }

            textLoc = new Point(textLoc.getX(), textLoc.getY() + 35);

            Font oldFont = graphics.getFont();

            graphics.setFont(new Font("Arial", Font.BOLD, 20));
            Point pointShadow = new Point(textLoc.getX() + 1, textLoc.getY() + 1);

            OverlayUtil.renderTextLocation(graphics, pointShadow, textOverlay, Color.BLACK);
            if (plugin.getNum_player_hits() == 5){
                OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.RED);
            }else{
                OverlayUtil.renderTextLocation(graphics, textLoc, textOverlay, Color.WHITE);
            }
            graphics.setFont(oldFont);
        }
    }

    private void renderObject(Graphics2D graphics, Color color, GameObject object){
        Polygon objectClickbox = (Polygon) object.getConvexHull();
        drawPolygon(graphics, objectClickbox, color);
    }


    private void renderActor(Graphics2D graphics, Color color, Actor actor){
        Polygon objectClickbox = (Polygon) actor.getConvexHull();
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
