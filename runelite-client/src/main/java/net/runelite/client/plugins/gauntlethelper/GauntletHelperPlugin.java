/* This entire plugin is copyrighted by Abiyaz Chowdhury 2019 */

package net.runelite.client.plugins.gauntlethelper;

import com.google.common.collect.ImmutableSet;
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
    private static final int BOW_ATTACK = 426;
    private static final int STAFF_ATTACK = 1167;
    private static final Set<Integer> MELEE_ANIMATIONS = ImmutableSet.of(395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440);
    private static final Set<Integer> PLAYER_ANIMATIONS = ImmutableSet.of(395, 401, 400, 401, 386, 390, 422, 423, 401, 428, 440, 426, 1167);
    private static final Set<Integer> ResourceIDs = new HashSet<>(Arrays.asList(ObjectID.CRYSTAL_DEPOSIT, ObjectID.CORRUPT_DEPOSIT, ObjectID.PHREN_ROOTS,
            ObjectID.PHREN_ROOTS_36066, ObjectID.FISHING_SPOT_36068, ObjectID.FISHING_SPOT_35971, ObjectID.GRYM_ROOT, ObjectID.GRYM_ROOT_36070,
            ObjectID.LINUM_TIRINUM, ObjectID.LINUM_TIRINUM_36072));

    @Getter
    private HashMap<GameTile, GameObject> resource_spots = new HashMap<>();

    @Getter
    private HashSet<NPC> monsters = new HashSet<>();

    @Getter
	private NPC hunllef = null;

    @Getter
    private HashSet<Tornado> tornados = new HashSet<>();

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private GauntletHelperOverlay overlay;

    @Inject
    private InfoBoxOverlay infoBoxOverlay;

    @Inject
    private MinimapOverlay minimapOverlay;

    @Inject
    private Client client;

    @Getter
    private GauntletSupplies supplies = new GauntletSupplies();

    @Getter
    private HashSet<NPC> bosses = new HashSet<>();

    private Item[] items = null;

    @Getter
    private boolean is_boss_using_range = true;

    @Getter
    private int num_boss_hits = 0;

    @Getter
    private int num_player_hits = 0;

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
        if (animationChanged.getActor() instanceof Player){
        	//System.out.println("Player animation changed. " + animationChanged.getActor().getAnimation() );
			int animation = animationChanged.getActor().getAnimation();
			//System.out.println("Player interacting  with boss");
			if (!PLAYER_ANIMATIONS.contains(animation) || !fightingBoss() || hunllef == null ){
				return;
			}
			//System.out.println("Valid animation.");
			switch(client.getNpcDefinition(hunllef.getId()).getOverheadIcon())
			{
				case MAGIC:
					if (animation != STAFF_ATTACK)
					{
						offprayerHit();
					}
					break;
				case MELEE:
					if (!MELEE_ANIMATIONS.contains(animation))
					{
						offprayerHit();
					}
					break;
				case RANGED:
					if (animation != BOW_ATTACK)
					{
						offprayerHit();
					}
					break;
				default:
					break;
			}
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
           resource_spots.put(new GameTile(event.getTile().getWorldLocation()),gameObject);
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
        resource_spots.remove(new GameTile(event.getTile().getWorldLocation()));
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned event){
        if (!isInGauntlet()){
            return;
        }
        NPC npc = event.getNpc();
        //System.out.println("NPC spawned " + npc.getName());
		if (npc.getName().toLowerCase().contains("hunllef")){
			hunllef = npc;
		}
        else if (npc.getId() == 9025 || npc.getId() == 9039){
            tornados.add(new Tornado(npc));
        }
        else if (npc.getName().toLowerCase().contains("beast") ||
            npc.getName().toLowerCase().contains("dragon") ||
            npc.getName().toLowerCase().contains("bear")) {
            bosses.add(npc);
        }else if (npc.getName().toLowerCase().contains("crystal")||
                npc.getName().toLowerCase().contains("corrupt")){
            if (!npc.getName().toLowerCase().contains("hunllef")){
                monsters.add(npc);
            }
        }
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned event){
        if (!isInGauntlet()){
            return;
        }
        NPC npc = event.getNpc();
        if (npc == hunllef){
        	hunllef = null;
        	return;
		}
        //System.out.println("NPC DEspawned " + npc.getName());
        tornados.remove(npc);
        bosses.remove(npc);
        monsters.remove(npc);
    }

    @Subscribe
    public void onGameTick(GameTick tick){
        if (!isInGauntlet()) {
            return;
        }
        for (Tornado tornado : tornados){
            tornado.updateTimeLeft();
        }
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
        num_player_hits = 0;
        is_boss_using_range = true;
        tool_storage = null;
        resource_spots.clear();
        bosses.clear();
        monsters.clear();
        supplies = new GauntletSupplies();
        items = null;
    }

    private void offprayerHit(){
    	//System.out.println("offprayerhit");
        num_player_hits++;
        if (num_player_hits == 6){
            num_player_hits = 0;
        }
    }

	boolean fightingBoss()
	{
		return client.getVar(Varbits.GAUNTLET_FINAL_ROOM_ENTERED) == 1;
	}


}
