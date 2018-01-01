---
name: PlayerItemPickupEvent
title: PlayerItemPickupEvent
subtitle: When a Player collects something
type: event
extends: Event
layout: module
properties:  
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that triggered this event.
    "
  - name: item
    type: "[DroppedItem](!SITE_URL!/modules/DroppedItem/)"
    access: r
    description: "The item that has been collected.
    "
functions:
---

The PlayerItemPickupEvent is fired whenever a [Player](/modules/Player) picks up
an [DroppedItem](/modules/DroppedItem).
