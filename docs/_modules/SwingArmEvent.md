---
name: SwingArmEvent
title: SwingArmEvent
subtitle:
type: event
extends: Event
layout: module
properties:
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that triggered this event.
    "
  - name: hand
    type: string
    access: r
    description: "The hand the player waved.
    Can be 'MAIN_HAND' or 'OFF_HAND'.
    "
  - name: item
    type: "[Item](!SITE_URL!/modules/Item/)"
    access: r
    description: "The item in the player's hand.
    "
functions:
---

The SwingArmEvent is fired whenever a [Player](/modules/Player) waves an arm.
This can be the left arm or the right arm. This event is fired on three occasions:
- just before the [RightClickBlockEvent](/modules/RightClickBlockEvent)
- just before the [LeftClickBlockEvent](/modules/LeftClickBlockEvent)
- when the player does a left-click into the air.
