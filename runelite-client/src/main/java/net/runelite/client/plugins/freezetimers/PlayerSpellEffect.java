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

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.GraphicID;

@AllArgsConstructor
public enum PlayerSpellEffect
{
	BIND("Bind", GraphicID.BIND, 5000, true, 0, TimerType.FREEZE),
	SNARE("Snare", GraphicID.SNARE, 10000, true, 1, TimerType.FREEZE),
	ENTANGLE("Entangle", GraphicID.ENTANGLE, 15000, true, 2, TimerType.FREEZE),
	RUSH("Ice Rush", GraphicID.ICE_RUSH, 5000, false, 3, TimerType.FREEZE),
	BURST("Ice Burst", GraphicID.ICE_BURST, 10000, false, 4, TimerType.FREEZE),
	BLITZ("Ice Blitz", GraphicID.ICE_BLITZ, 15000, false, 5, TimerType.FREEZE),
	BARRAGE("Ice Barrage", GraphicID.ICE_BARRAGE, 20000, false, 6, TimerType.FREEZE),
	TELEBLOCK("Teleblock", 345, 300000, true, 7, TimerType.TELEBLOCK),
	VENG("Vengeance", GraphicID.VENGEANCE, 30000, false, 8, TimerType.VENG),
	VENG_OTHER("Vengeance Other", GraphicID.VENGEANCE_OTHER, 30000, false, 9, TimerType.VENG),
	NONE("Nothing", -69, 420, true, 9999, TimerType.THIS_SHIT_BROKE);

	@Getter
	private final String name;
	@Getter
	private final int GraphicId;
	@Getter
	private final int timerLengthTicks;
	@Getter
	private boolean halvable;
	@Getter
	private final int spriteIdx;
	@Getter
	private final TimerType type;

	public static PlayerSpellEffect getFromGraphic(int spotAnim)
	{
		for (PlayerSpellEffect effect : values())
		{
			if (effect.getGraphicId() == spotAnim)
			{
				return effect;
			}
		}
		return NONE;
	}

}
