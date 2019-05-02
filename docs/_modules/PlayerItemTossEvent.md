---
name: PlayerItemTossEvent
title: PlayerItemTossEvent
subtitle: 
type: event
extends: EntityEvent
layout: module
properties:
  - name: item
    type: '[DroppedItem](/modules/DroppedItem)'
    access: r
    description: |
        This is the [dropped item](/modules/DroppedItem) being tossed.
  - name: player
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The [player](/modules/Player) tossing the item.
functions:
---

The <span class="notranslate">PlayerItemTossEvent</span> is fired whenever a player tosses (for
example by pressing 'Q') an item or drag-n-drops a stack of items outside the inventory GUI
screens.

Canceling the event will stop the items from entering the world, but will not prevent them being
removed from the inventory - and thus removed from the system.
