---
name: PlayerItemPickupEvent
title: PlayerItemPickupEvent
subtitle: When a Player Collects Something
type: event
extends: Event
layout: module
properties:
  - name: 'item'
    type: '[DroppedItem](/modules/DroppedItem)'
    access: r
    description: |
        The item that has been collected.
  - name: 'player'
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The player that triggered this event.
functions:
---

The <span class="notranslate">PlayerItemPickupEvent</span> is fired whenever a
[Player](/modules/Player) picks up an [DroppedItem](/modules/DroppedItem).
