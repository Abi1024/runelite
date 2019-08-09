/* This entire plugin is copyrighted by Abiyaz Chowdhury 2019 */

package net.runelite.client.plugins.gauntlethelper;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.util.*;

import static java.lang.Math.abs;
import static net.runelite.api.AnimationID.IDLE;

@PluginDescriptor(
        name = "Gauntlet Helper",
        description = "Provides various overlays to assist with the gauntlet minigame",
        tags = {"overlay", "bosses"},
        enabledByDefault = false
)

public class GauntletHelperPlugin extends Plugin {
    private static final Set<Integer> ResourceIDs = new HashSet<>(Arrays.asList(ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT, ObjectID.PHREN_ROOTS,
            ObjectID.PHREN_ROOTS_36066, ObjectID.FISHING_SPOT_36068, ObjectID.FISHING_SPOT_35971, ObjectID.GRYM_ROOT, ObjectID.GRYM_ROOT_36070,
            ObjectID.LINUM_TIRINUM, ObjectID.LINUM_TIRINUM_36072));

    @Getter
    private Set<GameObject> resource_spots = new HashSet<>();

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletHelperOverlay overlay;

    @Inject
    private InfoBoxOverlay infoBoxOverlay;

    @Inject
    private MinimapOverlay minimapOverlay;

    private int counter = 0;

    @Inject
    private Client client;

    @Getter
    private GauntletSupplies supplies = new GauntletSupplies();

    @Getter
    private HashSet<NPC> bosses = new HashSet<>();

    private Item[] items = null;

    public boolean is_boss_using_range = true;
    private int num_boss_hits = 0;

    private GameObject tool_storage = null;

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(infoBoxOverlay);
        overlayManager.add(minimapOverlay);
        resetPlugin();
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(infoBoxOverlay);
        overlayManager.remove(minimapOverlay);
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
        if (items == null){
            if (event.getItemContainer() != null){
                items = event.getItemContainer().getItems();
            }
            return;
        }
        Item[] new_items = event.getItemContainer().getItems();
        if (!isInMainRoom()){
            supplies.fish += getAmountDifference(new_items,items,ItemID.RAW_PADDLEFISH);
            supplies.ore += getAmountDifference(new_items,items,ItemID.CRYSTAL_ORE);
            supplies.ore += getAmountDifference(new_items,items,ItemID.CORRUPTED_ORE);
            supplies.bark += getAmountDifference(new_items,items,ItemID.PHREN_BARK);
            supplies.bark += getAmountDifference(new_items,items,ItemID.PHREN_BARK_23878);
            supplies.linum += getAmountDifference(new_items,items,ItemID.LINUM_TIRINUM);
            supplies.linum += getAmountDifference(new_items,items,ItemID.LINUM_TIRINUM_23876);
            supplies.herbs += getAmountDifference(new_items,items,ItemID.GRYM_LEAF);
            supplies.herbs += getAmountDifference(new_items,items,ItemID.GRYM_LEAF_23875);
            if (getAmountDifference(new_items,items,ItemID.GRYM_POTION_UNF) > 0){
                supplies.herbs += getAmountDifference(new_items,items,ItemID.GRYM_POTION_UNF);
            }
            supplies.shards += getAmountDifference(new_items,items,ItemID.CRYSTAL_SHARDS);
            supplies.shards += getAmountDifference(new_items,items,ItemID.CORRUPTED_SHARDS);
            supplies.weapons += getAmountDifference(new_items,items,ItemID.WEAPON_FRAME);
            supplies.weapons += getAmountDifference(new_items,items,ItemID.WEAPON_FRAME_23871);
        }
        items = new_items;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        if (!isInGauntlet()){
            return;
        }
        final GameObject gameObject = event.getGameObject();
        if (gameObject.getId() == ObjectID.TOOL_STORAGE || gameObject.getId() == ObjectID.TOOL_STORAGE_36074){
            tool_storage = gameObject;
            return;
        }
        if (ResourceIDs.contains(gameObject.getId())){
            System.out.println("RESOURCE SPAWNED: " + client.getObjectDefinition(gameObject.getId()).getName() + " Tile: "
            + gameObject.getWorldLocation().getX() + " " + gameObject.getWorldLocation().getY());
            resource_spots.add(gameObject);
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick){
        if (!isInGauntlet()) {
            return;
        }
        int count = 1;
        counter++;
        if (counter == 10){
            counter = 0;
            System.out.println("Resource spots size: " + resource_spots.size());
            for (GameObject spot: resource_spots){
                System.out.println("Spot " + count + ": " + client.getObjectDefinition(spot.getId()).getName() + " Tile: " +
                        spot.getWorldLocation().getX() + " " + spot.getWorldLocation().getY());
                count++;
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (!isInGauntlet()){
            return;
        }
        final GameObject gameObject = event.getGameObject();
        if (gameObject.getId() == ObjectID.TOOL_STORAGE || gameObject.getId() == ObjectID.TOOL_STORAGE_36074){
            tool_storage = null;
            return;
        }
        if (ResourceIDs.contains(gameObject.getId())){
            System.out.println("RESOURCE DISAPPEARED: " + client.getObjectDefinition(gameObject.getId()).getName() + " Tile: "
                    + gameObject.getWorldLocation().getX() + " " + gameObject.getWorldLocation().getY());
            resource_spots.removeIf(object -> object == gameObject);
        }
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event){
        if (!isInGauntlet()){
            return;
        }
        NPC npc = event.getNpc();
        if (npc.getName().toLowerCase().contains("beast") ||
            npc.getName().toLowerCase().contains("dragon") ||
            npc.getName().toLowerCase().contains("bear")) {
            bosses.add(npc);
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event){
        if (!isInGauntlet()){
            return;
        }
        NPC npc = event.getNpc();
        bosses.remove(npc);
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

    boolean isInGauntlet(){
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

    boolean isInCorruptedGauntlet(){
        if (client.getMapRegions() != null){
            if (client.getMapRegions().length > 0){
                return client.getMapRegions()[0] == 7768;
            }
        }
        return false;
    }

    private int getAmountInventory(Item[] items, int itemID){
        int count = 0;
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

    private void resetPlugin(){
        num_boss_hits = 0;
        is_boss_using_range = true;
        tool_storage = null;
        resource_spots.clear();
        bosses.clear();
        supplies = new GauntletSupplies();
        items = null;
    }


}
