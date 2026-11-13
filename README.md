# Hide Overload Shock

Drinking an overload shocks you five times, and every shock freezes your character in place for a moment. If you are walking or running when it happens, you stop dead, then snap forward a moment later to catch up. Five times per dose. It feels awful when you are trying to reposition in a raid or dodge something.

This plugin gets rid of that.

## What it does

- **No more stopping mid-walk.** Your character keeps moving smoothly through all five shocks.
- **No skeleton flash.** The electric skeleton effect drawn over your character on each shock is hidden too.

## What it does not do

- It does not change the overload itself. You still take the damage from each shock, exactly as before. This is purely how your character looks on your own screen.
- It only affects your own character, not other players.

## Why it happens

The shock plays a full-body animation, and the game client does not let a character walk while a full-body animation like that is playing. So it parks you on your path until the animation ends, then moves you along to where the server says you are. Drinking the potion itself does not do this, because that animation only moves your upper body.

The plugin cancels the shock animation the moment it starts, so there is nothing holding your character in place.

## Good to know

The overload reuses the electric shock from Killerwatts in Draynor Manor, so if you ever get shocked by one of those, that effect is hidden as well.

Works with every overload: Nightmare Zone, Chambers of Xeric and Deadman.
