---
name: UseItemStartEvent
title: UseItemStartEvent
subtitle: When an Entity Starts Using an Item
type: event
extends: UseItemEvent
layout: module
properties:
functions:
---

The <span class="notranslate">UseItemStartEvent</span> class is fired when a [Mob](/modules/Mob)
or [Player](/modules/Player) starts using an [Item](/modules/Item). Setting the
[duration](/modules/UseItemEvent#duration) to zero or less cancels this event.

#### Example

Prevent those nasty skeletons from shooting you.

```lua
Events.on('UseItemStartEvent'):call(function(event)
  if event.entity.name == 'Skeleton' then
    event.canceled = true
  end
end)
```
