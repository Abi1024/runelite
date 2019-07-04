package net.runelite.client.plugins.hatcounter;

import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class HatCounterOverlay extends Overlay
{
    private final PanelComponent panelComponent = new PanelComponent();
    private final HatCounterPlugin plugin;
    private final HatCounterConfig config;
    private final ItemManager manager;


    @Inject
    private HatCounterOverlay(HatCounterPlugin plugin, HatCounterConfig config, ItemManager manager)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setPriority(OverlayPriority.LOW);
        this.plugin = plugin;
        this.config = config;
        this.manager = manager;
        panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
        panelComponent.setWrapping(4);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Hat counter overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<Integer, Integer> hats = plugin.getHats();
        if (hats.isEmpty())
        {
            return null;
        }
        if (config.getshowOnlyInPVP() && !plugin.isShouldDisplayOverlay()){
            return null;
        }
        panelComponent.getChildren().clear();

        //System.out.println(hats.toString());

        for (Map.Entry<Integer, Integer> hat : hats.entrySet())
        {

            // Only display team capes that have a count greater than the configured minimum
            if (hat.getValue() < config.getMinimumHatCount())
            {
                continue;
            }
            final int hatID = hat.getKey();
            String item_name = manager.getItemComposition(hatID).getName().toLowerCase();
            if (Arrays.asList(config.getIgnoredHeadgear().toLowerCase().split(",")).contains(item_name)){
                //System.out.println("ITEM NAME: " + item_name);
                continue;
            }
            panelComponent.getChildren().add(new ImageComponent(manager.getImage(hatID, hat.getValue(), true)));
        }

        return panelComponent.render(graphics);
    }
}
