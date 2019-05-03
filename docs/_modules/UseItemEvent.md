---
name: UseItemEvent
title: UseItemEvent
subtitle: When an Entity Uses an Item
type: event
extends: LivingEvent
layout: module
properties:
  - name: 'duration'
    type: 'number (int)'
    access: r/w
    description: |
        The 'duration' is the number of remaining game ticks until this event will terminate normally
        and the use is finished.
       
        #### Example
       
        Increase the time it takes to eat a golden apple to 5 seconds (100 gameticks), gold is pretty
        hard to chew anyway.
       
        ```lua
        Events.on('UseItemStartEvent'):call(function(event)
          if event.item.id == 'golden_apple' then
            event.duration = 100
          end
        end)
        ```
  - name: 'item'
    type: '[Item](/modules/Item)'
    access: r
    description: |
        This is the used [item](/modules/Item).
functions:
---

The <span class="notranslate">UseItemEvent</span> class is the base class of events about
[Item](/modules/Item) usage.

The <span class="notranslate">UseItemEvent</span> is fired when a [Mob](/modules/Mob) or
[Player](/modules/Player) uses an [Item](/modules/Item).

Typical scenarios are:
<ul>
<li>Drawing a bow</li>
<li>Eating food</li>
<li>Drinking potions or milk</li>
<li>Guarding with a shield</li>
</ul>

Setting the [duration](/modules/UseItemEvent#duration) to zero or less cancels this event.
