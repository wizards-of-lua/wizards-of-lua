---
name: SpellBreakEvent
title: SpellBreakEvent
subtitle:
type: event
extends: Event
layout: module
properties:
  - name: spell
    type: "[Spell](/modules/Spell/)"
    access: r
    description: |
      The [Spell](/modules/Spell) that is being terminated.
functions:
---

The <span class="notranslate">SpellBreakEvent</span> class informs about the termination of a
[Spell](/modules/Spell).

A spell can intercept its own break event and do some clean-up before it is finally terminated.

#### Example
```lua
Events.on('SpellBreakEvent'):call(function(event)
  if event.spell == spell then
    -- do some clean-up here
  end
end)
```
