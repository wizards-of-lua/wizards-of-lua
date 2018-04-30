---
name: UseItemStopEvent
title: UseItemStopEvent
subtitle: When an Entity Stops Using an Item
type: event
extends: UseItemEvent
layout: module
properties:
functions:
---

The <span class="notranslate">UseItemStopEvent</span> class is fired when a [Mob](/modules/Mob)
or [Player](/modules/Player) stops using an [Item](/modules/Item) without
[finishing](/modules/UseItemFinishEvent) it. Currently the only vanilla item that is affected by
canceling this event is the bow. If this event is canceled the bow does not shoot an arrow.

#### Example

Print a message when the player stops eating a golden apple.

```lua
Events.on('UseItemStopEvent'):call(function(event)
  if event.item.id == 'golden_apple' then
    print('Are you not hungry?')
  end
end)
```
