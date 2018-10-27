---
name: UseItemTickEvent
title: UseItemTickEvent
subtitle: While an Entity Uses an Item
type: event
extends: UseItemEvent
layout: module
properties:
functions:
---

The <span class="notranslate">UseItemTickEvent</span> class is fired every gametick while a
[Mob](/modules/Mob) or [Player](/modules/Player) uses an [Item](/modules/Item). Setting the
[duration](/modules/UseItemEvent#duration) to zero or less cancels this event.

#### Example

Print messages while the player is eating a golden apple.

```lua
Events.on('UseItemTickEvent'):call(function(event)
  if event.item.id == 'golden_apple' then
    print('Om nom '..event.duration)
  end
end)
```
