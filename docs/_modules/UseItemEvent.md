---
name: UseItemEvent
title: UseItemEvent
subtitle: When an Entity Uses an Item
type: event
extends: LivingEvent
layout: module
properties:
  - name: item
    type: "[Item](/modules/Item/)"
    access: r
    description: "The item that is in use.
    "
  - name: duration
    type: number
    access: r/w
    description: "The 'duration' is the number of remaining game ticks until this event will terminate normally.
    "
functions:
---

The <span class="notranslate">UseItemEvent</span> is fired when a [Mob](/modules/Mob)
or [Player](/modules/Player) uses an [Item](/modules/Item). Setting the
[duration](/modules/UseItemEvent#duration) to zero or less cancels this event.
