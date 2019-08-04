/* This entire plugin is copyrighted by Abiyaz Chowdhury 2019 */

package net.runelite.client.plugins.gauntlethelper;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;

import java.awt.*;
import java.awt.geom.Area;
import java.util.HashMap;

import static java.lang.Math.abs;
import static net.runelite.api.AnimationID.IDLE;

@PluginDescriptor(
        name = "Gauntlet Helper",
        description = "Provides various overlays to assist with the gauntlet minigame",
        tags = {"overlay", "bosses"},
        enabledByDefault = false
)

public class GauntletHelperPlugin extends Plugin {
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletHelperOverlay overlay;

    @Inject
    private InfoBoxOverlay infoBoxOverlay;

    @Inject
    private ItemManager manager;

    @Inject
    private Client client;

    @Getter
    private GauntletSupplies supplies = new GauntletSupplies();

    @Getter
    private HashMap<GameTile,GameObject> fishing_spots = new HashMap<GameTile,GameObject>();

    @Getter
    private HashMap<GameTile,GameObject> mining_spots = new HashMap<GameTile,GameObject>();

    @Getter
    private HashMap<GameTile,GameObject> bark_spots = new HashMap<GameTile,GameObject>();

    @Getter
    private HashMap<GameTile,GameObject> linum_spots = new HashMap<GameTile,GameObject>();

    @Getter
    private HashMap<GameTile,GameObject> herb_spots = new HashMap<GameTile,GameObject>();

    private Item[] items = null;

    private Area main_area = new Area(new Rectangle(10930,8354,11,11));
    private Area corrupted_area = new Area(new Rectangle(8242,8770,11,11));

    public boolean is_boss_using_range = true;
    private int num_boss_hits = 0;
    private ItemContainer inventory;

    private GameObject tool_storage = null;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(infoBoxOverlay);
        resetPlugin();
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(infoBoxOverlay);
        resetPlugin();
    }

    @Provides
    GauntletHelperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(GauntletHelperConfig.class);
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged){
        if (!isInGauntlet()){
            return;
        }
        if (animationChanged.getActor().getAnimation() == IDLE){
            return;
        }
        if (animationChanged.getActor() == client.getLocalPlayer()){
            //System.out.println("Player animation: " + animationChanged.getActor().getAnimation());
            return;
        }
        if (animationChanged.getActor().getName() != null){
            if (animationChanged.getActor().getName().contains("Hunllef")){
                switch(animationChanged.getActor().getAnimation()){
                    //gauntlet attack animations
                    case 8419:
                    case 8418:
                        num_boss_hits++;
                        if (num_boss_hits == 4) {
                            num_boss_hits = 0;
                            is_boss_using_range = !is_boss_using_range;
                        }
                        break;
                    default:
                        break;
                }
                System.out.println("Hunllef animation: " + animationChanged.getActor().getAnimation());
                System.out.println("Num_boss_hits: " + num_boss_hits);
            }
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged){
        if(!isInGauntlet()){
            resetPlugin();
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event){
        if (!isInGauntlet()){
            return;
        }
        System.out.println("item changed");
        if (items == null){
            if (event.getItemContainer() != null){
                items = event.getItemContainer().getItems();
            }
            return;
        }
        Item[] new_items = event.getItemContainer().getItems();
        if (!isInMainRoom()){
            System.out.println("not in main room!");
            System.out.println("old supply: " + getAmountInventory(items,ItemID.CORRUPTED_ORE) );
            System.out.println("new supply: " + getAmountInventory(new_items,ItemID.CORRUPTED_ORE) );
            supplies.fish += getAmountDifference(new_items,items,ItemID.RAW_PADDLEFISH);
            supplies.ore += getAmountDifference(new_items,items,ItemID.CRYSTAL_ORE);
            supplies.ore += getAmountDifference(new_items,items,ItemID.CORRUPTED_ORE);
            supplies.bark += getAmountDifference(new_items,items,ItemID.PHREN_BARK);
            supplies.bark += getAmountDifference(new_items,items,ItemID.PHREN_BARK_23878);
            supplies.linum += getAmountDifference(new_items,items,ItemID.LINUM_TIRINUM);
            supplies.linum += getAmountDifference(new_items,items,ItemID.LINUM_TIRINUM_23876);
            supplies.herbs += getAmountDifference(new_items,items,ItemID.GRYM_LEAF);
            supplies.herbs += getAmountDifference(new_items,items,ItemID.GRYM_LEAF_23875);
            supplies.shards += getAmountDifference(new_items,items,ItemID.CRYSTAL_SHARDS);
            supplies.shards += getAmountDifference(new_items,items,ItemID.CORRUPTED_SHARDS);
            supplies.weapons += getAmountDifference(new_items,items,ItemID.WEAPON_FRAME);
            supplies.weapons += getAmountDifference(new_items,items,ItemID.WEAPON_FRAME_23871);
        }
        System.out.println("Ore supply: " + supplies.ore);
        items = new_items;
        System.out.println("Inventory supply: " + getAmountInventory(items,ItemID.CORRUPTED_ORE));
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (!isInGauntlet()){
            return;
        }
        GameObject gameObject = event.getGameObject();
        switch (gameObject.getId()){
            case ObjectID.TOOL_STORAGE:
            case ObjectID.TOOL_STORAGE_36074:
                tool_storage = gameObject;
                break;
            case ObjectID.FISHING_SPOT_36068:
            case ObjectID.FISHING_SPOT_35971:
                fishing_spots.put(new GameTile(event.getGameObject().getWorldLocation()),event.getGameObject());
                break;
            case ObjectID.CRYSTAL_DEPOSIT:
            case ObjectID.CORRUPT_DEPOSIT:
                mining_spots.put(new GameTile(event.getGameObject().getWorldLocation()),event.getGameObject());
                break;
            case ObjectID.LINUM_TIRINUM:
            case ObjectID.LINUM_TIRINUM_36072:
                linum_spots.put(new GameTile(event.getGameObject().getWorldLocation()),event.getGameObject());
                break;
            case ObjectID.PHREN_ROOTS:
            case ObjectID.PHREN_ROOTS_36066:
                bark_spots.put(new GameTile(event.getGameObject().getWorldLocation()),event.getGameObject());
                break;
            case ObjectID.GRYM_ROOT:
            case ObjectID.GRYM_ROOT_36070:
                herb_spots.put(new GameTile(event.getGameObject().getWorldLocation()),event.getGameObject());
                break;

            default:
                break;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (!isInGauntlet()){
            return;
        }
        GameObject gameObject = event.getGameObject();
        switch (gameObject.getId()){
            case ObjectID.TOOL_STORAGE:
            case ObjectID.TOOL_STORAGE_36074:
                tool_storage = null;
                break;
            case ObjectID.FISHING_SPOT_36068:
            case ObjectID.FISHING_SPOT_35971:
                fishing_spots.remove(new GameTile(event.getGameObject().getWorldLocation()));
                break;
            case ObjectID.CRYSTAL_DEPOSIT:
            case ObjectID.CORRUPT_DEPOSIT:
                mining_spots.remove(new GameTile(event.getGameObject().getWorldLocation()));
                break;
            case ObjectID.LINUM_TIRINUM:
            case ObjectID.LINUM_TIRINUM_36072:
                linum_spots.remove(new GameTile(event.getGameObject().getWorldLocation()));
                break;
            case ObjectID.PHREN_ROOTS:
            case ObjectID.PHREN_ROOTS_36066:
                bark_spots.remove(new GameTile(event.getGameObject().getWorldLocation()));
                break;
            case ObjectID.GRYM_ROOT:
            case ObjectID.GRYM_ROOT_36070:
                herb_spots.remove(new GameTile(event.getGameObject().getWorldLocation()));
                break;
            default:
                break;
        }
    }


    @Subscribe
    public void onGameTick(GameTick tick){
        if (!isInGauntlet()){
            return;
        }
    }

    private void resetPlugin(){
        num_boss_hits = 0;
        is_boss_using_range = true;
        tool_storage = null;
        fishing_spots.clear();
        mining_spots.clear();
        bark_spots.clear();
        linum_spots.clear();
        herb_spots.clear();
        supplies = new GauntletSupplies();
        items = null;
    }

    private int supNorm(WorldPoint w1, WorldPoint w2){
        int x = abs(w1.getX()-w2.getX());
        int y = abs(w1.getY()-w2.getY());
        return Math.max(x,y);
    }

    private boolean isInMainRoom(){
        if (tool_storage == null){
            return false;
        }
        WorldPoint toolLocation = tool_storage.getWorldLocation();
        if (supNorm(toolLocation,client.getLocalPlayer().getWorldLocation()) < 7){
            return true;
        }
        return false;
    }

    public boolean isInGauntlet(){
        if (client.getMapRegions() != null){
            if (client.getMapRegions().length > 0){
                switch (client.getMapRegions()[0]){
                    case 7512:
                    case 7768:
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    public boolean isInCorruptedGauntlet(){
        if (client.getMapRegions() != null){
            if (client.getMapRegions().length > 0){
                switch (client.getMapRegions()[0]){
                    case 7768:
                        return true;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    private int getAmountInventory(Item[] items, int itemID){
        int count =  0;
        for (Item item : items){
            if (item.getId() == itemID){
                count += item.getQuantity();
            }
        }
        return count;
    }

    private int getAmountDifference(Item[] new_items, Item[] old_items, int itemID){
        return getAmountInventory(new_items,itemID)-getAmountInventory(old_items,itemID);
    }

    private String printLocation(WorldPoint w){
        return (w.getX() + " " + w.getY());
    }




}
