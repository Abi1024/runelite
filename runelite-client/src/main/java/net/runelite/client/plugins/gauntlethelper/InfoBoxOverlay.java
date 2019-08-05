package net.runelite.client.plugins.gauntlethelper;

import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

public class InfoBoxOverlay extends Overlay {
    private final GauntletHelperConfig config;
    private final GauntletHelperPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    private final ItemManager manager;

    @Inject
    private Client client;

    @Inject
    InfoBoxOverlay(Client client, GauntletHelperConfig config, GauntletHelperPlugin plugin, ItemManager manager)
    {
        this.client = client;
        this.config = config;
        this.plugin = plugin;
        this.manager = manager;
        setPosition(OverlayPosition.TOP_CENTER);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setWrapping(7);
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!plugin.isInGauntlet()){
            return null;
        }
        if (config.suppliesInfoBox()){
            renderSupplyInfobox(graphics);
        }
        return null;

    }

    public void renderSupplyInfobox(Graphics2D graphics){
        panelComponent.getChildren().clear();
        if (plugin.getSupplies().fish < config.num_fish()){
            int amount = config.num_fish()-plugin.getSupplies().fish;
            panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.RAW_PADDLEFISH, amount, true)));
        }
        if (plugin.getSupplies().ore < config.num_resources()){
            int amount = config.num_resources()-plugin.getSupplies().ore;
            if (plugin.isInCorruptedGauntlet()){
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.CORRUPTED_ORE, amount, true)));
            }else{
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.CRYSTAL_ORE, amount, true)));
            }
        }
        if (plugin.getSupplies().bark < config.num_resources()){
            int amount = config.num_resources()-plugin.getSupplies().bark;
            if (plugin.isInCorruptedGauntlet()){
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.PHREN_BARK, amount, true)));
            }else{
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.PHREN_BARK_23878, amount, true)));
            }
        }
        if (plugin.getSupplies().linum < config.num_resources()){
            int amount = config.num_resources()-plugin.getSupplies().linum;
            panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.LINUM_TIRINUM, amount, true)));
        }
        if (plugin.getSupplies().herbs < config.num_herbs()){
            int amount = config.num_herbs()-plugin.getSupplies().herbs;
            if (plugin.isInCorruptedGauntlet()){
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.GRYM_LEAF, amount, true)));
            }else{
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.GRYM_LEAF_23875, amount, true)));
            }

        }
        if (plugin.getSupplies().shards < config.num_shards()){
            int amount = config.num_shards()-plugin.getSupplies().shards;
            if (plugin.isInCorruptedGauntlet()){
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.CORRUPTED_SHARDS, amount, true)));
            }else{
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.CRYSTAL_SHARDS, amount, true)));
            }
        }
        if (plugin.getSupplies().weapons < config.num_weapons()){
            int amount = config.num_weapons()-plugin.getSupplies().weapons;
            if (plugin.isInCorruptedGauntlet()){
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.WEAPON_FRAME, amount, true)));
            }else{
                panelComponent.getChildren().add(new ImageComponent(manager.getImage(ItemID.WEAPON_FRAME_23871, amount, true)));
            }

        }
        panelComponent.render(graphics);
    }
}
