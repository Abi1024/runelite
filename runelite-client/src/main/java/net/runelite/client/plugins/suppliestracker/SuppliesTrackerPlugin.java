/*
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Sir Girion <https://github.com/darakelian>
 * Copyright (c) 2018, Davis Cook <https://github.com/daviscook477>
 * Copyright (c) 2018, Daddy Dozer <Whitedylan7@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *	list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *	this list of conditions and the following disclaimer in the documentation
 *	and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.suppliestracker;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.item.ItemPrice;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static net.runelite.api.AnimationID.*;
import static net.runelite.api.ItemID.*;
import static net.runelite.api.ObjectID.CANNON_BASE;
import static net.runelite.api.ProjectileID.CANNONBALL;
import static net.runelite.api.ProjectileID.GRANITE_CANNONBALL;
import static net.runelite.client.plugins.suppliestracker.ActionType.*;


@PluginDescriptor(
	name = "Supplies Used Tracker",
	description = "Tracks supplies used during the session",
	tags = {"cost"},
	enabledByDefault = false
)

@Slf4j
public class SuppliesTrackerPlugin extends Plugin
{
    @Inject
    private ConfigManager configManager;

    private static final String CONFIG_GROUP = "suppliestracker";
    private static final String CONFIG_KEY = "supplies";

	private static final String POTION_PATTERN = "[(]\\d[)]";

	private static final String EAT_PATTERN = "^eat";
	private static final String DRINK_PATTERN = "^drink";
	private static final String TELEPORT_PATTERN = "^teleport";
	private static final String TELETAB_PATTERN = "^break";
	private static final String SPELL_PATTERN = "^cast|^grand\\sexchange|^outside|^seers|^yanille";

	private static final int EQUIPMENT_MAINHAND_SLOT = EquipmentInventorySlot.WEAPON.getSlotIdx();
	private static final int EQUIPMENT_AMMO_SLOT = EquipmentInventorySlot.AMMO.getSlotIdx();
	private static final int EQUIPMENT_CAPE_SLOT = EquipmentInventorySlot.CAPE.getSlotIdx();

	private static final double NO_AVAS_PERCENT = 1.0;
	private static final double ASSEMBLER_PERCENT = 0.20;
	private static final double ACCUMULATOR_PERCENT = 0.28;
	private static final double ATTRACTOR_PERCENT = 0.40;

	private static final int BLOWPIPE_TICKS_RAPID_PVM = 2;
	private static final int BLOWPIPE_TICKS_RAPID_PVP = 3;
	private static final int BLOWPIPE_TICKS_NORMAL_PVM = 3;
	private static final int BLOWPIPE_TICKS_NORMAL_PVP = 4;

	private static final double SCALES_PERCENT = 0.66;

	private static final int POTION_DOSES = 4, CAKE_DOSES = 3, PIZZA_PIE_DOSES = 2;

	private static final Random random = new Random();

	private static final int[] THROWING_IDS = new int[]{BRONZE_DART, IRON_DART, STEEL_DART, BLACK_DART, MITHRIL_DART, ADAMANT_DART, RUNE_DART, DRAGON_DART, BRONZE_KNIFE, IRON_KNIFE, STEEL_KNIFE, BLACK_KNIFE, MITHRIL_KNIFE, ADAMANT_KNIFE, RUNE_KNIFE, BRONZE_THROWNAXE, IRON_THROWNAXE, STEEL_THROWNAXE, MITHRIL_THROWNAXE, ADAMANT_THROWNAXE, RUNE_THROWNAXE, DRAGON_KNIFE, DRAGON_KNIFE_22812, DRAGON_KNIFE_22814, DRAGON_KNIFEP_22808, DRAGON_KNIFEP_22810, DRAGON_KNIFEP, DRAGON_THROWNAXE, CHINCHOMPA_10033, RED_CHINCHOMPA_10034, BLACK_CHINCHOMPA};
	private static final int[] RUNE_IDS = new int[]{AIR_RUNE, WATER_RUNE, EARTH_RUNE, FIRE_RUNE, MIND_RUNE, BODY_RUNE, COSMIC_RUNE, CHAOS_RUNE, NATURE_RUNE, LAW_RUNE, DEATH_RUNE, ASTRAL_RUNE, BLOOD_RUNE, SOUL_RUNE, WRATH_RUNE, MIST_RUNE, DUST_RUNE, MUD_RUNE, SMOKE_RUNE, STEAM_RUNE, LAVA_RUNE};

	//time in milliseconds between varbitchanged and animationchanged when casting spell with runepouch runes
	private static final int DELAY = 100;

	//time in milliseconds between menuentryclicked and varbitchanged when casting spell with runepouch runes
	private static final int DELAY_2 = 700;

	private boolean cannonPlaced;

	private WorldPoint cannonPosition = null;

	//Hold Supply Data
	private static HashMap<Integer, SuppliesTrackerItem> suppliesEntry = new HashMap<>();
	private ItemContainer old;
	private Deque<MenuAction> actionStack = new ArrayDeque<>();
	private Deque<RunePouchState> pouchChangedEvents = new ArrayDeque<>();
	private RunePouchState pouchChangedtrackEvent = null;
	private int ammoId = 0;
	private int ammoAmount = 0;
	private int thrownId = 0;
	private int thrownAmount = 0;
	private boolean ammoLoaded = false;
	private boolean throwingAmmoLoaded = false;
	private boolean mainHandThrowing = false;
	private int mainHand = 0;
	private SuppliesTrackerPanel panel;
	private NavigationButton navButton;
	private String[] RAIDS_CONSUMABLES = new String[]{"xeric's", "elder", "twisted", "revitalisation", "overload", "prayer enhance", "pysk", "suphi", "leckish", "brawk", "mycil", "roqed", "kyren", "guanic", "prael", "giral", "phluxia", "kryket", "murng", "psykk"};

	private int attackStyleVarbit = -1;
	private int ticks = 0;
	private int ticksInAnimation;

	private static final Varbits[] AMOUNT_VARBITS =
			{
					Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3
			};
	private static final Varbits[] RUNE_VARBITS =
			{
					Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3
			};

	private RunePouchState storedPouchState = new RunePouchState();

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private SuppliesTrackerConfig config;

	@Inject
	private Client client;


	@Override
	protected void startUp() throws Exception
	{
		panel = new SuppliesTrackerPanel(itemManager, this);
		final BufferedImage header = ImageUtil.getResourceStreamFromClass(getClass(), "panel_icon.png");
		panel.loadHeaderIcon(header);
		final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "panel_icon.png");

		navButton = NavigationButton.builder()
			.tooltip("Supplies Tracker")
			.icon(icon)
			.priority(5)
			.panel(panel)
			.build();

		clientToolbar.addNavigation(navButton);
		loadConfig();
		if (client.getGameState() == GameState.LOGGED_IN){
			storedPouchState = getPouchState();
		}
	}

	private RunePouchState getPouchState(){
		int[] new_amounts = new int[3];
		int[] new_ids = new int[3];
		for (int i = 0; i < 3; i++){
			new_amounts[i] = client.getVar(AMOUNT_VARBITS[i]);
			if (new_amounts[i] <= 0){
				new_ids[i] = -1;
			}else{
				if(client.getVar(RUNE_VARBITS[i]) == 0) {
					new_ids[i] = -1;
				}else{
					new_ids[i] = Runes.getRune(client.getVar(RUNE_VARBITS[i])).getItemId();
				}
			}
		}
		return new RunePouchState(new_amounts,new_ids,System.currentTimeMillis());
	}

	@Override
	protected void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Provides
    SuppliesTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SuppliesTrackerConfig.class);
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (pouchChangedtrackEvent != null && System.currentTimeMillis() - pouchChangedtrackEvent.getTime() >= DELAY_2){
			pouchChangedtrackEvent = null;
		}
		Player player = client.getLocalPlayer();
		if (player.getAnimation() == BLOWPIPE_ATTACK)
		{
			ticks++;
        }
		if (ticks == ticksInAnimation && (player.getAnimation() == BLOWPIPE_ATTACK))
		{
			double ava_percent = getAccumulatorPercent();
			// randomize the usage of supplies since we CANNOT actually get real supplies used
			if (random.nextDouble() <= ava_percent)
			{
				buildEntries(config.blowpipeAmmo().getDartID());

			}
			if (random.nextDouble() <= SCALES_PERCENT)
			{
				buildEntries(ZULRAHS_SCALES);
			}
			ticks = 0;
		}
	}

	/**
	 * checks the player's cape slot to determine what percent of their darts are lost
	 * - where lost means either break or drop to floor
	 *
	 * @return the percent lost
	 */
	private double getAccumulatorPercent()
	{
		double percent = NO_AVAS_PERCENT;
		ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
		if (equipment != null && equipment.getItems().length > EQUIPMENT_CAPE_SLOT)
		{
			int capeID = equipment.getItems()[EQUIPMENT_CAPE_SLOT].getId();
			switch (capeID)
			{
				case AVAS_ASSEMBLER:
				case ASSEMBLER_MAX_CAPE:
					percent = ASSEMBLER_PERCENT;
					break;
				case AVAS_ACCUMULATOR:
				case ACCUMULATOR_MAX_CAPE:
					// TODO: the ranging cape can be used as an attractor so this could be wrong
				case RANGING_CAPE:
					percent = ACCUMULATOR_PERCENT;
					break;
				case AVAS_ATTRACTOR:
					percent = ATTRACTOR_PERCENT;
					break;
			}
		}
		return percent;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		if (attackStyleVarbit == -1 || attackStyleVarbit != client.getVar(VarPlayer.ATTACK_STYLE))
		{
			attackStyleVarbit = client.getVar(VarPlayer.ATTACK_STYLE);
			if (attackStyleVarbit == 0 || attackStyleVarbit == 3)
			{
				ticksInAnimation = BLOWPIPE_TICKS_NORMAL_PVM;
				if (client.getLocalPlayer() != null &&
					client.getLocalPlayer().getInteracting() instanceof Player)
				{
					ticksInAnimation = BLOWPIPE_TICKS_NORMAL_PVP;
				}
			}
			else if (attackStyleVarbit == 1)
			{
				ticksInAnimation = BLOWPIPE_TICKS_RAPID_PVM;
				if (client.getLocalPlayer() != null &&
					client.getLocalPlayer().getInteracting() instanceof Player)
				{
					ticksInAnimation = BLOWPIPE_TICKS_RAPID_PVP;
				}
			}
		}
		boolean flush_events = true;
		RunePouchState currentPouchState = getPouchState();
		if(pouchChangedtrackEvent != null){
			RunePouchState pouch = pouchChangedtrackEvent;
			if(!RunePouchState.equal_pouches(pouch,currentPouchState)){
				for (int i = 0; i < 3; i++){
					if (currentPouchState.getIds()[i] != pouch.getIds()[i]){
						buildEntries(pouch.getIds()[i],pouch.getAmount()[i]);
					}else{
						int amount_changed = pouch.getAmount()[i] - currentPouchState.getAmount()[i];
						if (amount_changed > 0){
							buildEntries(pouch.getIds()[i],amount_changed);
						}
					}
				}
				pouchChangedtrackEvent = new RunePouchState(currentPouchState.getAmount(),currentPouchState.getIds(),pouch.getTime());
			}
		}
		if (!RunePouchState.equal_pouches(currentPouchState, storedPouchState)) {
			int[] amount_change = new int[3];
			int[] ids = new int[3];
			for (int i = 0; i < 3; i++){
				if (currentPouchState.getIds()[i] != storedPouchState.getIds()[i]){
					amount_change[i] = storedPouchState.getAmount()[i];
				}else{
					amount_change[i] = storedPouchState.getAmount()[i] - currentPouchState.getAmount()[i];
				}
				ids[i] = storedPouchState.getIds()[i];
			}
			pouchChangedEvents.push(new RunePouchState(amount_change,ids,System.currentTimeMillis()));
			storedPouchState = currentPouchState;
		}
		flush_events = true;
		while (flush_events){
			if (!pouchChangedEvents.isEmpty()){
				if (System.currentTimeMillis() - pouchChangedEvents.peek().getTime() > DELAY){
					pouchChangedEvents.pop();
				}else{
					flush_events = false;
				}
			}else{
				flush_events = false;
			}
		}

	}

	/**
	 * Checks for changes between the provided inventories in runes specifically to add those runes
	 * to the supply tracker
	 * <p>
	 * we can't in general just check for when inventory slots change but this method is only run
	 * immediately after the player performs a cast animation or cast menu click/entry
	 *
	 * @param itemContainer the new inventory
	 * @param oldInv        the old inventory
	 */
	private void checkUsedRunes(ItemContainer itemContainer, Item[] oldInv)
	{
		try
		{
			for (int i = 0; i < itemContainer.getItems().length; i++)
			{
				Item newItem = itemContainer.getItems()[i];
				Item oldItem = oldInv[i];
				boolean isRune = false;
				for (int runeId : RUNE_IDS)
				{
					if (oldItem.getId() == runeId)
					{
						isRune = true;
					}
				}
				if (isRune && (newItem.getId() != oldItem.getId() || newItem.getQuantity() != oldItem.getQuantity()))
				{
					int quantity = oldItem.getQuantity();
					if (newItem.getId() == oldItem.getId())
					{
						quantity -= newItem.getQuantity();
					}
					buildEntries(oldItem.getId(), quantity);
				}
			}
		}
		catch (IndexOutOfBoundsException ignored)
		{
		}
	}



	/*@Subscribe
	public void onCannonballFired(CannonballFired cannonballFired)
	{
		buildEntries(CANNONBALL);
	}
*/
	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (animationChanged.getActor() != client.getLocalPlayer()){
			return;
		}
		int animationID = animationChanged.getActor().getAnimation();

		switch(animationID){
			//test for charged craw's bow attack
			case(426): {
				ItemContainer equipedItems = client.getItemContainer(InventoryID.EQUIPMENT);
				if (equipedItems.getItems().length > EQUIPMENT_MAINHAND_SLOT) {
					if (equipedItems.getItems()[EQUIPMENT_MAINHAND_SLOT].getId() == CRAWS_BOW) {
						buildEntries(REVENANT_ETHER);
					}
				}
				break;
			}
			//test for viggora's chainmace attack
			case(245): {
				ItemContainer equipedItems = client.getItemContainer(InventoryID.EQUIPMENT);
				if (equipedItems.getItems().length > EQUIPMENT_MAINHAND_SLOT) {
					if (equipedItems.getItems()[EQUIPMENT_MAINHAND_SLOT].getId() == VIGGORAS_CHAINMACE) {
						buildEntries(REVENANT_ETHER);
					}
				}
				break;
			}
			case (HIGH_LEVEL_MAGIC_ATTACK):
				switch (mainHand) {
					case TRIDENT_OF_THE_SEAS_E:
					case TRIDENT_OF_THE_SEAS:
					case TRIDENT_OF_THE_SEAS_FULL:
						buildEntries(CHAOS_RUNE);
						buildEntries(DEATH_RUNE);
						buildEntries(FIRE_RUNE, 5);
						buildEntries(COINS_995, 10);
						break;
					case TRIDENT_OF_THE_SWAMP_E:
					case TRIDENT_OF_THE_SWAMP:
					case UNCHARGED_TOXIC_TRIDENT:
					case UNCHARGED_TOXIC_TRIDENT_E:
						buildEntries(CHAOS_RUNE);
						buildEntries(DEATH_RUNE);
						buildEntries(FIRE_RUNE, 5);
						buildEntries(ZULRAHS_SCALES);
						break;
					case SANGUINESTI_STAFF:
					case SANGUINESTI_STAFF_UNCHARGED:
						buildEntries(BLOOD_RUNE, 3);
						break;
					default:
						processCombatSpell();
						break;
				}
				break;
			case LOW_LEVEL_MAGIC_ATTACK:
			case VERY_HIGH_LEVEL_MAGIC_ATTACK:
				processCombatSpell();
				break;
			default:
				break;
		}
	}

	public void processCombatSpell(){
		old = client.getItemContainer(InventoryID.INVENTORY);
		if (old != null && old.getItems() != null && actionStack.stream().noneMatch(a ->
				a.getType() == CAST))
		{
			MenuAction newAction = new MenuAction(CAST, old.getItems());
			actionStack.push(newAction);
		}
		while (!pouchChangedEvents.isEmpty()){
			RunePouchState event = pouchChangedEvents.pop();
			if (System.currentTimeMillis() - event.getTime() < DELAY){
				for (int i = 0; i < 3; i++){
					if (event.getAmount()[i] <= 0){
						continue;
					}
					buildEntries(event.getIds()[i],event.getAmount()[i]);
				}
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		ItemContainer itemContainer = itemContainerChanged.getItemContainer();

		if (itemContainer == client.getItemContainer(InventoryID.INVENTORY) && old != null && !actionStack.isEmpty())
		{
			while (!actionStack.isEmpty())
			{
				MenuAction frame = actionStack.pop();
				ActionType type = frame.getType();
				MenuAction.ItemAction itemFrame;
				Item[] oldInv = frame.getOldInventory();
				switch (type)
				{
					case CONSUMABLE:
						itemFrame = (MenuAction.ItemAction) frame;
						int nextItem = itemFrame.getItemID();
						int nextSlot = itemFrame.getSlot();
						if (itemContainer.getItems()[nextSlot].getId() != oldInv[nextSlot].getId())
						{
							buildEntries(nextItem);
						}
						break;
					case TELEPORT:
						itemFrame = (MenuAction.ItemAction) frame;
						int teleid = itemFrame.getItemID();
						int slot = itemFrame.getSlot();
						if (itemContainer.getItems()[slot].getId() != oldInv[slot].getId() || itemContainer.getItems()[slot].getQuantity() != oldInv[slot].getQuantity())
						{
							buildEntries(teleid);
						}
						break;
					case CAST:
						checkUsedRunes(itemContainer, oldInv);
						break;
				}
			}
		}

		if (itemContainer == client.getItemContainer(InventoryID.EQUIPMENT))
		{
			//set mainhand for trident tracking
			if (itemContainer.getItems().length > EQUIPMENT_MAINHAND_SLOT)
			{
				mainHand = itemContainer.getItems()[EQUIPMENT_MAINHAND_SLOT].getId();
				net.runelite.api.Item mainHandItem = itemContainer.getItems()[EQUIPMENT_MAINHAND_SLOT];
				for (int throwingIDs : THROWING_IDS)
				{
					if (mainHand == throwingIDs)
					{
						mainHandThrowing = true;
						break;
					}
					else
					{
						mainHandThrowing = false;
					}
				}
				if (mainHandThrowing)
				{
					if (throwingAmmoLoaded)
					{
						if (thrownId == mainHandItem.getId())
						{
							if (thrownAmount - 1 == mainHandItem.getQuantity())
							{
								buildEntries(mainHandItem.getId());
								thrownAmount = mainHandItem.getQuantity();
							}
							else
							{
								thrownAmount = mainHandItem.getQuantity();
							}
						}
						else
						{
							thrownId = mainHandItem.getId();
							thrownAmount = mainHandItem.getQuantity();
						}
					}
					else
					{
						thrownId = mainHandItem.getId();
						thrownAmount = mainHandItem.getQuantity();
						throwingAmmoLoaded = true;
					}
				}else{
					throwingAmmoLoaded = false;
				}
			}
			//Ammo tracking
			if (itemContainer.getItems().length > EQUIPMENT_AMMO_SLOT)
			{
				net.runelite.api.Item ammoSlot = itemContainer.getItems()[EQUIPMENT_AMMO_SLOT];
				if (ammoSlot != null)
				{
					if (ammoLoaded)
					{
						if (ammoId == ammoSlot.getId())
						{
							if (ammoAmount - 1 == ammoSlot.getQuantity())
							{
								buildEntries(ammoSlot.getId());
								ammoAmount = ammoSlot.getQuantity();
							}
							else
							{
								ammoAmount = ammoSlot.getQuantity();
							}
						}
						else
						{
							ammoId = ammoSlot.getId();
							ammoAmount = ammoSlot.getQuantity();
						}
					}
					else
					{
						ammoId = ammoSlot.getId();
						ammoAmount = ammoSlot.getQuantity();
						ammoLoaded = true;
					}
				}
			}

		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject gameObject = event.getGameObject();

		Player localPlayer = client.getLocalPlayer();
		if (gameObject.getId() == CANNON_BASE && !cannonPlaced)
		{
			if (localPlayer.getWorldLocation().distanceTo(gameObject.getWorldLocation()) <= 2
					&& localPlayer.getAnimation() == AnimationID.BURYING_BONES)
			{
				cannonPosition = gameObject.getWorldLocation();
			}
		}
	}

	@Subscribe
	public void onMenuOptionClicked(final MenuOptionClicked event)
    {
		// Uses stacks to push/pop for tick eating
		// Create pattern to find eat/drink at beginning
		Pattern eatPattern = Pattern.compile(EAT_PATTERN);
		Pattern drinkPattern = Pattern.compile(DRINK_PATTERN);
		if (eatPattern.matcher(event.getMenuTarget().toLowerCase()).find() || drinkPattern.matcher(event.getMenuTarget().toLowerCase()).find())
		{
			if (actionStack.stream().noneMatch(a ->
			{
				if (a instanceof MenuAction.ItemAction)
				{
					MenuAction.ItemAction i = (MenuAction.ItemAction) a;
					return i.getItemID() == event.getId();
				}
				return false;
			}))
			{
				old = client.getItemContainer(InventoryID.INVENTORY);
				int slot = event.getActionParam();
				if (old.getItems() != null)
				{
					int pushItem = old.getItems()[event.getActionParam()].getId();
					MenuAction newAction = new MenuAction.ItemAction(CONSUMABLE, old.getItems(), pushItem, slot);
					actionStack.push(newAction);
				}
			}
		}

		// Create pattern for teleport scrolls and tabs
		Pattern teleportPattern = Pattern.compile(TELEPORT_PATTERN);
		Pattern teletabPattern = Pattern.compile(TELETAB_PATTERN);
		if (teleportPattern.matcher(event.getMenuTarget().toLowerCase()).find() ||
			teletabPattern.matcher(event.getMenuTarget().toLowerCase()).find())
		{
			old = client.getItemContainer(InventoryID.INVENTORY);

			// Makes stack only contains one teleport type to stop from adding multiple of one teleport
			if (old != null && old.getItems() != null && actionStack.stream().noneMatch(a ->
				a.getType() == TELEPORT))
			{
				int teleid = event.getId();
				MenuAction newAction = new MenuAction.ItemAction(TELEPORT, old.getItems(), teleid, event.getActionParam());
				actionStack.push(newAction);
			}
		}

		// Create pattern for spell cast
		Pattern spellPattern = Pattern.compile(SPELL_PATTERN);
		// note that here we look at the option not target b/c the option for all spells is cast
		// but the target differs based on each spell name
		if (spellPattern.matcher(event.getMenuOption().toLowerCase()).find())
		{
			old = client.getItemContainer(InventoryID.INVENTORY);
			if (old != null && old.getItems() != null && actionStack.stream().noneMatch(a ->
				a.getType() == CAST))
			{
				MenuAction newAction = new MenuAction(CAST, old.getItems());
				actionStack.push(newAction);
			}
			if (pouchChangedtrackEvent == null || System.currentTimeMillis() - pouchChangedtrackEvent.getTime() >= DELAY_2){
				pouchChangedtrackEvent = getPouchState();
			}
		}
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();

		if ((projectile.getId() == ProjectileID.CANNONBALL || projectile.getId() == ProjectileID.GRANITE_CANNONBALL) && cannonPosition != null)
		{
			WorldPoint projectileLoc = WorldPoint.fromLocal(client, projectile.getX1(), projectile.getY1(), client.getPlane());
			if(cannonPosition == null){
				return;
			}
			//Check to see if projectile x,y is 0 else it will continuously decrease while ball is flying.
			if (projectileLoc.equals(cannonPosition) && projectile.getX() == 0 && projectile.getY() == 0)
			{
				switch(projectile.getId()){
					case(CANNONBALL):
						buildEntries(ItemID.CANNONBALL);
						break;
					case(GRANITE_CANNONBALL):
						buildEntries(ItemID.GRANITE_CANNONBALL);
				}
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event){
		if (event.getMessage().contains("You pick up the cannon")
				|| event.getMessage().contains("Your cannon has decayed. Speak to Nulodion to get a new one!"))
		{
			cannonPosition = null;
		}
	}



	/**
	 * Checks if item name is potion
	 *
	 * @param name the name of the item
	 * @return if the item is a potion - i.e. has a (1) (2) (3) or (4) in the name
	 */
	static boolean isPotion(String name)
	{
		return name.contains("(4)") || name.contains("(3)") || name.contains("(2)") || name.contains("(1)");
	}

	/**
	 * Checks if item name is pizza or pie
	 *
	 * @param name the name of the item
	 * @return if the item is a pizza or a pie - i.e. has pizza or pie in the name
	 */
	static boolean isPizzaPie(String name)
	{
		return name.toLowerCase().contains("pizza") || name.toLowerCase().contains(" pie");
	}

	static boolean isCake(String name, int itemId)
	{
		return name.toLowerCase().contains("cake") || itemId == ItemID.CHOCOLATE_SLICE;
	}

	/**
	 * correct prices for potions, pizzas pies, and cakes
	 * tracker tracks each dose of a potion/pizza/pie/cake as an entire one
	 * so must divide price by total amount of doses in each
	 * this is necessary b/c the most correct/accurate price for these resources is the
	 * full price not the 1-dose price
	 *
	 * @param name   the item name
	 * @param itemId the item id
	 * @param price  the current calculated price
	 * @return the price modified by the number of doses
	 */
	private long scalePriceByDoses(String name, int itemId, long price)
	{
		if (isPotion(name))
		{
			return price / POTION_DOSES;
		}
		if (isPizzaPie(name))
		{
			return price / PIZZA_PIE_DOSES;
		}
		if (isCake(name, itemId))
		{
			return price / CAKE_DOSES;
		}
		return price;
	}

	/**
	 * Add an item to the supply tracker (with 1 count for that item)
	 *
	 * @param itemId the id of the item
	 */
	private void buildEntries(int itemId)
	{
		buildEntries(itemId, 1);
	}

	/**
	 * Add an item to the supply tracker
	 *
	 * @param itemId the id of the item
	 * @param count  the amount of the item to add to the tracker
	 */
	private void buildEntries(int itemId, int count)
	{
		final ItemComposition itemComposition = itemManager.getItemComposition(itemId);
		String name = itemComposition.getName();
		long calculatedPrice;

		for (String raidsConsumables : RAIDS_CONSUMABLES)
            if (name.toLowerCase().contains(raidsConsumables)) {
                return;
            }

		// convert potions, pizzas/pies, and cakes to their full equivalents
		// e.g. a half pizza becomes full pizza, 3 dose potion becomes 4, etc...
		if (isPotion(name))
		{
			name = name.replaceAll(POTION_PATTERN, "(4)");
			itemId = getPotionID(name);
		}
		if (isPizzaPie(name))
		{
			itemId = getFullVersionItemID(itemId);
			name = itemManager.getItemComposition(itemId).getName();
		}
		if (isCake(name, itemId))
		{
			itemId = getFullVersionItemID(itemId);
			name = itemManager.getItemComposition(itemId).getName();
		}

		int newQuantity;
		if (suppliesEntry.containsKey(itemId))
		{
			newQuantity = suppliesEntry.get(itemId).getQuantity() + count;
		}
		else
		{
			newQuantity = count;
		}

		// calculate price for amount of doses used
		calculatedPrice = ((long) itemManager.getItemPrice(itemId)) * ((long) newQuantity);
		calculatedPrice = scalePriceByDoses(name, itemId, calculatedPrice);

		// write the new quantity and calculated price for this entry
		SuppliesTrackerItem newEntry = new SuppliesTrackerItem(
			itemId,
			name,
			newQuantity,
			calculatedPrice);

		suppliesEntry.put(itemId, newEntry);
		updateConfig();
		SwingUtilities.invokeLater(() ->
			panel.addItem(newEntry));
	}

    private void updateConfig()
    {
        if (suppliesEntry.isEmpty()){
            configManager.unsetConfiguration(CONFIG_GROUP, CONFIG_KEY);
        }

        final Gson gson = new Gson();
        final String json = gson.toJson(suppliesEntry);
        configManager.setConfiguration(CONFIG_GROUP, CONFIG_KEY, json);
    }

    private void loadConfig()
    {
        // serialize the internal data structure from the json in the configuration
        final String json = configManager.getConfiguration(CONFIG_GROUP, CONFIG_KEY);
        if (json == null || json.isEmpty())
        {
            suppliesEntry.clear();
        }
        else
        {
            final Gson gson = new Gson();
            Type type = new TypeToken<HashMap<Integer, SuppliesTrackerItem>>()
            {

            }.getType();
            suppliesEntry.clear();
            suppliesEntry.putAll(gson.fromJson(json, type));
        }

        for (final Integer itemID : suppliesEntry.keySet())
        {
            SwingUtilities.invokeLater(() ->
                    panel.addItem(suppliesEntry.get(itemID)));
        }

    }

	/**
	 * reset all item stacks
	 */
	void clearSupplies()
	{
		suppliesEntry.clear();
		updateConfig();
	}

	/**
	 * reset an individual item stack
	 *
	 * @param itemId the id of the item stack
	 */
	void clearItem(int itemId)
	{
		suppliesEntry.remove(itemId);
        updateConfig();
	}

	/**
	 * Gets the item id that matches the provided name within the itemManager
	 *
	 * @param name the given name
	 * @return the item id for this name
	 */
	private int getPotionID(String name)
	{
		int itemId = 0;

		List<ItemPrice> items = itemManager.search(name);
		for (ItemPrice item : items)
		{
			if (item.getName().contains(name))
			{
				itemId = item.getId();
			}
		}
		return itemId;
	}

	/**
	 * Takes the item id of a partial item (e.g. 1 dose potion, 1/2 a pizza, etc...) and returns
	 * the corresponding full item
	 *
	 * @param itemId the partial item id
	 * @return the full item id
	 */
	private int getFullVersionItemID(int itemId)
	{
		switch (itemId)
		{
			case _12_ANCHOVY_PIZZA:
				itemId = ANCHOVY_PIZZA;
				break;
			case _12_MEAT_PIZZA:
				itemId = MEAT_PIZZA;
				break;
			case _12_PINEAPPLE_PIZZA:
				itemId = PINEAPPLE_PIZZA;
				break;
			case _12_PLAIN_PIZZA:
				itemId = PLAIN_PIZZA;
				break;
			case HALF_A_REDBERRY_PIE:
				itemId = REDBERRY_PIE;
				break;
			case HALF_A_GARDEN_PIE:
				itemId = GARDEN_PIE;
				break;
			case HALF_A_SUMMER_PIE:
				itemId = SUMMER_PIE;
				break;
			case HALF_A_FISH_PIE:
				itemId = FISH_PIE;
				break;
			case HALF_A_BOTANICAL_PIE:
				itemId = BOTANICAL_PIE;
				break;
			case HALF_A_MUSHROOM_PIE:
				itemId = MUSHROOM_PIE;
				break;
			case HALF_AN_ADMIRAL_PIE:
				itemId = ADMIRAL_PIE;
				break;
			case HALF_A_WILD_PIE:
				itemId = WILD_PIE;
				break;
			case HALF_AN_APPLE_PIE:
				itemId = APPLE_PIE;
				break;
			case HALF_A_MEAT_PIE:
				itemId = MEAT_PIE;
				break;
			// note behavior of case means both below cases return CAKE
			case _23_CAKE:
			case SLICE_OF_CAKE:
				itemId = CAKE;
				break;
			case _23_CHOCOLATE_CAKE:
			case CHOCOLATE_SLICE:
				itemId = CHOCOLATE_CAKE;
				break;
		}
		return itemId;
	}
}
