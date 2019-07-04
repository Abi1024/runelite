package net.runelite.client.plugins.lootassist;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentHashMap;

@PluginDescriptor(
	name = "Loot Assist Plugin",
	description = "Creates a tile overlay with a timer that counts down to when the loot will appear to you",
	tags = {"pklite", "loot", "looting", "loot assist", "assist", "loot assist"},
	enabledByDefault = false
)

public class LootAssistPlugin extends Plugin
{
    @Inject
    Client client;

	@Inject
    OverlayManager overlayManager;

	@Inject
	LootAssistOverlay lootAssistOverlay;

	@Getter
	private boolean shouldDisplayOverlay = false;

	static ConcurrentHashMap<WorldPoint, LootPile> lootPiles = new ConcurrentHashMap<>();

	@Provides
    LootAssitConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(LootAssitConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(lootAssistOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		lootPiles.clear();
		overlayManager.remove(lootAssistOverlay);
	}

	@Subscribe
	public void onGameTick(GameTick tickEvent) {
		boolean inDeadman = client.getWorldType().stream().anyMatch(x ->
				x == WorldType.DEADMAN || x == WorldType.SEASONAL_DEADMAN);
		boolean inPvp = client.getWorldType().stream().anyMatch(x ->
				x == WorldType.PVP || x == WorldType.HIGH_RISK);
		shouldDisplayOverlay = false;
		if (inDeadman || inPvp){
			shouldDisplayOverlay = true;
		}else if (MapLocations.isLocationInWilderness(client.getLocalPlayer().getWorldLocation())){
			shouldDisplayOverlay = true;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() != GameState.LOADING && event.getGameState() != GameState.LOGGED_IN) {
		    //System.out.println(event.getGameState().name());
			lootPiles.clear();
		}
        //System.out.println("NEW GAME STATE: " + event.getGameState().name());
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		final Actor actor = event.getActor();
		if (actor.getAnimation() == AnimationID.DEATH && actor instanceof Player)
		{
			LootPile pile = new LootPile(actor.getWorldLocation(), actor.getName());
			lootPiles.put(pile.getLocation(), pile);
		}
	}


}
