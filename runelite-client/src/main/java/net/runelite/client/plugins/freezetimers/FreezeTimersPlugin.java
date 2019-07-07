/*
 * Copyright (c) 2019, ganom <https://github.com/Ganom>
 * Copyright (c) 2019, pklite <https://github.com/pklite/pklite>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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
package net.runelite.client.plugins.freezetimers;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;

@PluginDescriptor(
	name = "Freeze Timers",
	description = "Shows a freeze timer overlay on players",
	tags = {"freeze", "timers", "barrage", "teleblock", "pklite"},
	enabledByDefault = false
)

public class FreezeTimersPlugin extends Plugin
{

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Timers timers;

	@Inject
	private PrayerTracker prayerTracker;

	@Inject
	private FreezeTimersOverlay overlay;

    private HashMap<Actor, WorldPoint> playerLocations = new HashMap<>();

	public void startUp()
	{
		overlayManager.add(overlay);
		playerLocations.clear();
		timers.clearAllTimers();
	}

	public void shutDown()
	{
		overlayManager.remove(overlay);
		playerLocations.clear();
		timers.clearAllTimers();
	}

	@Provides
	public FreezeTimersConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FreezeTimersConfig.class);
	}

	@Subscribe
    public void onAnimationChanged(AnimationChanged animationChanged){
	        //if no timers are currently active for this player, there is nothing to be done
	        if (timers.areAllTimersZero(animationChanged.getActor())){
	            return;
            }
            //but if this player dies, all his timers should be cleared.
            else if (animationChanged.getActor().getAnimation() == AnimationID.DEATH){
                timers.removeAllTimers(animationChanged.getActor());
                playerLocations.remove(animationChanged.getActor());
            }
    }

	@Subscribe
	public void onGraphicChanged(GraphicChanged graphicChanged)
	{
		int oldGraphic = prayerTracker.getGraphicLastTick(graphicChanged.getActor());
		int newGraphic = graphicChanged.getActor().getGraphic();
		if (oldGraphic == newGraphic)
		{
			return;
		}
		PlayerSpellEffect effect = PlayerSpellEffect.getFromGraphic(newGraphic);
		if (effect == PlayerSpellEffect.NONE)
		{
			return;
		}
		long length = effect.getTimerLengthTicks();
		//if praying mage and it affects the spell
		if (effect.isHalvable() && prayerTracker.getPrayerIconLastTick(graphicChanged.getActor()) == 2)
		{
			length /= 2;
		}
		//if already frozen
		if (timers.getTimerEnd(graphicChanged.getActor(), effect.getType()) > System.currentTimeMillis())
		{
			return;
		}
		timers.setTimerEnd(graphicChanged.getActor(), effect.getType(),
			System.currentTimeMillis() + length);
		playerLocations.put(graphicChanged.getActor(),graphicChanged.getActor().getWorldLocation());
	}

	@Subscribe
	public void onGameTick(GameTick tickEvent)
	{
		timers.gameTick();
		prayerTracker.gameTick();
		for (Actor actor : client.getPlayers())
		{
			if (prayerTracker.getGraphicLastTick(actor) != actor.getGraphic())
			{
				GraphicChanged callback = new GraphicChanged();
				callback.setActor(actor);
				client.getCallbacks().post(callback);
			}
			//if this player is frozen and has moved after being frozen, remove the freeze timer
            if (timers.getTimerEnd(actor,TimerType.FREEZE) > System.currentTimeMillis())
            {
                if (!playerLocations.get(actor).equals(actor.getWorldLocation())){
                    timers.removeTimer(actor,TimerType.FREEZE);
                    playerLocations.remove(actor);
                }
            }
            //if this player is TBed but no longer in a PVP zone, remove the TB timer
            if (timers.getTimerEnd(actor,TimerType.TELEBLOCK) > System.currentTimeMillis())
            {
                //if in wilderness keep the TB timer
                if (MapLocations.isLocationInWilderness(actor.getWorldLocation())){
                    continue;
                }
                //if not in wilderness, but in a deadman world and not in a deadman safe zone
                boolean inDeadman = client.getWorldType().stream().anyMatch(x ->
                        x == WorldType.DEADMAN || x == WorldType.SEASONAL_DEADMAN);
                if (inDeadman && !MapLocations.isLocationInDeadmanSafe(actor.getWorldLocation())){
                    continue;
                }
                boolean inPvp = client.getWorldType().stream().anyMatch(x ->
                        x == WorldType.PVP || x == WorldType.HIGH_RISK);
                if (inPvp && !MapLocations.isLocationInPVPSafeZone(actor.getWorldLocation())){
                    continue;
                }
                timers.removeTimer(actor,TimerType.TELEBLOCK);
            }
		}
	}

	@Subscribe
	public void onPlayerDespawned(PlayerDespawned playerDespawned)
	{
		final Player player = playerDespawned.getPlayer();
		timers.removeAllTimers(player);
		// All despawns ok: death, teleports, log out, runs away from screen
		//this.remove(player);
	}

	//if someone uses vengeance and then the spell is triggered, we use the chat to detect this and remove the timer
    @Subscribe
    public void onOverheadTextChanged(OverheadTextChanged event)
    {
        if (!event.getOverheadText().equals("Taste vengeance!")){
            return;
        }
        Actor actor = event.getActor();
        if ((actor != null) && (timers.getTimerEnd(actor,TimerType.VENG) >= System.currentTimeMillis())){
            timers.removeAllTimers(actor);
            playerLocations.remove(actor);
        }
    }

	public void clearLocation(Actor actor){
	    playerLocations.remove(actor);
    }

	/*public void remove(Actor actor)
	{
		freezes.remove(actor.getName());
	}*/

}
