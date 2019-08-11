package net.runelite.client.plugins.gauntlethelper;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.NPC;

@Getter(AccessLevel.PACKAGE)
class Tornado
{
    private NPC npc;
    private int timeLeft;

    Tornado(NPC npc)
    {
        this.npc = npc;
        this.timeLeft = 20;
    }

    void updateTimeLeft()
    {
        if (timeLeft > 0)
        {
            timeLeft--;
        }
    }
}