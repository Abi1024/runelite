/*
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

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

@Slf4j
@Singleton
public class Timers
{

	@Inject
	private Client client;

	private HashMap<Actor, HashMap<TimerType, Long>> timerMap = new HashMap<>();

	public void gameTick()
	{

	}

	public void setTimerEnd(Actor actor, TimerType type, long n)
	{
		if (!timerMap.containsKey(actor))
		{
			timerMap.put(actor, new HashMap<>());
		}
		timerMap.get(actor).put(type, n);
	}

	public long getTimerEnd(Actor actor, TimerType type)
	{
		if (!timerMap.containsKey(actor))
		{
			timerMap.put(actor, new HashMap<>());
		}
		return timerMap.get(actor).getOrDefault(type, (long) 0);
	}

	public void removeAllTimers(Actor actor){
		if (!timerMap.containsKey(actor))
		{
			return;
		}
		timerMap.remove(actor);
	}

	public void removeTimer(Actor actor, TimerType type){
		if (!timerMap.containsKey(actor)){
			return;
		}
		if (!timerMap.get(actor).containsKey(type)){
			return;
		}
		timerMap.get(actor).remove(type);
	}

	public boolean areAllTimersZero(Actor actor)
	{
		for (TimerType type : TimerType.values())
		{
			if (getTimerEnd(actor, type) != 0)
			{
				return false;
			}
		}
		return true;
	}

	public void clearAllTimers(){
		timerMap.clear();
	}

	public Actor getActor(String name){
		for (Actor actor : timerMap.keySet()){
			if (actor.getName().equals(name)){
				return actor;
			}
		}
		return null;
	}

}
