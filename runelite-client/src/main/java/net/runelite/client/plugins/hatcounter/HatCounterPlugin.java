/*
 * Copyright (c) 2017, Devin French <https://github.com/devinfrench>
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
package net.runelite.client.plugins.hatcounter;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Hat Counter",
        description = "Show the different hats in your area and the amount of each",
        tags = {"overlay", "players"},
        enabledByDefault = false
)

public class HatCounterPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private HatCounterOverlay overlay;

    @Getter
    private Map<Integer, Integer> hats = new HashMap<>();

    @Getter
    private boolean shouldDisplayOverlay = false;

    @Provides
    HatCounterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(HatCounterConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        hats.clear();
    }

    @Schedule(
            period = 1800,
            unit = ChronoUnit.MILLIS
    )
    public void update()
    {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
        List<Player> players = client.getPlayers();
        hats.clear();
        for (Player player : players)
        {
            int rawID = -1;
            if (player != null){
                if (player.getPlayerComposition() != null){
                    rawID = player.getPlayerComposition().getEquipmentIds()[0];
                }else{
                    continue;
                }
            }else {
                continue;
            }
            if (rawID == -1){
                continue;
            }
            int hatID = (rawID > 512) ? rawID - 512 : 0;
            //System.out.println(player.getName());
            //System.out.println(String.valueOf(rawID));
            //System.out.println(Arrays.toString(player.getPlayerComposition().getEquipmentIds()));
            if (hatID <= 0) {
                continue;
            }
            if (hats.containsKey(hatID))
            {
                hats.put(hatID, hats.get(hatID) + 1);
            }
            else
            {
                hats.put(hatID, 1);
                }
        }
        // Sort teams by value in descending order and then by key in ascending order, limited to 5 entries
        hats = hats.entrySet().stream()
                .sorted(
                        Comparator.comparing(Map.Entry<Integer, Integer>::getValue, Comparator.reverseOrder())
                                .thenComparingInt(Map.Entry::getKey)
                )
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        //System.out.println("HATS");
        //System.out.println(hats.toString());
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


}
