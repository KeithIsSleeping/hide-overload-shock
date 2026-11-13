/*
 * Copyright (c) 2026, KeithIsSleeping
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
package com.hideoverloadshock;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ActorSpotAnim;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.BeforeRender;
import net.runelite.api.gameval.AnimationID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

/**
 * Stops the movement stall, and hides the electric-shock effect, your character gets after
 * drinking an overload.
 *
 * <p>The overload's five self-damage shocks (2 ticks apart) reuse the Killerwatt
 * electrocution assets: animation {@code HUMAN_KILLERWATT_ELECTRICSHOCK} (3170) and the
 * skeleton graphic {@code SKELETON_KILLERWATT_ELECTRICSHOCK} (560) drawn over the player.
 * Confirmed from captures: 740 overload drinks each produced exactly five 560s two ticks
 * apart, nearly all paired with 3170 on the same tick.</p>
 *
 * <p><b>The stall</b>, which is the real problem: read from the game cache, 3170 is a
 * full-body animation (no {@code interleaveLeave}) with its movement priorities left unset,
 * which the client defaults to 0 - "may not walk while this plays". So each shock freezes a
 * moving player on their path for its length (~20 client cycles), after which the client
 * catches the player up - a visible pause-and-jump, five times per drink. Drinking itself
 * (829) has an interleave mask, defaults to 2, and doesn't stall. Cancelling 3170 the moment
 * it starts removes the stall; the movement itself is untouched.</p>
 *
 * <p>Purely client-side and cosmetic - the shocks still happen and still deal their damage.
 * The graphic is removed in {@link BeforeRender}, right before each frame is drawn, so it is
 * never rendered at all. Applies to the local player only. Because the assets are shared, a
 * real Killerwatt shock is hidden too.</p>
 */
@PluginDescriptor(
	name = "Hide Overload Shock",
	description = "Stops your character pausing mid-walk on each overload shock, and hides the skeleton shock effect",
	tags = {"overload", "nmz", "cox", "raids", "shock", "skeleton", "animation", "hide", "stall"}
)
public class HideOverloadShockPlugin extends Plugin
{
	private static final int SHOCK_ANIMATION = AnimationID.HUMAN_KILLERWATT_ELECTRICSHOCK;
	private static final int SHOCK_GRAPHIC = SpotanimID.SKELETON_KILLERWATT_ELECTRICSHOCK;

	@Inject
	private Client client;

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor actor = event.getActor();
		if (actor == client.getLocalPlayer() && actor.getAnimation() == SHOCK_ANIMATION)
		{
			actor.setAnimation(-1);
		}
	}

	@Subscribe
	public void onBeforeRender(BeforeRender event)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		// Collected first: removing while iterating the spotanim table would break the iteration.
		List<Long> keys = null;
		for (ActorSpotAnim spotAnim : player.getSpotAnims())
		{
			if (spotAnim.getId() == SHOCK_GRAPHIC)
			{
				if (keys == null)
				{
					keys = new ArrayList<>(1);
				}
				keys.add(spotAnim.getHash());
			}
		}
		if (keys != null)
		{
			for (long key : keys)
			{
				player.removeSpotAnim((int) key);
			}
		}
	}
}
