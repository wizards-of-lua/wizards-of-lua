---
name: LeftClickBlockEvent
title: LeftClickBlockEvent
subtitle: When a Player Left-Clicks on a Block
type: event
extends: PlayerInteractEvent
layout: module
properties:
  - name: 'hitVec'
    type: '[Vec3](/modules/Vec3)'
    access: r
    description: |
        The exact position the player clicked at.
       
        #### Example
       
        Creating some particle effect at the left-click hit position.
       
        ```lua
        local queue = Events.collect("LeftClickBlockEvent")
        while true do
          local event = queue:next()
          local v = event.hitVec
          spell:execute([[
            /particle angryVillager %s %s %s 0 0 0 0 1 true
          ]], v.x, v.y, v.z)
        end
        ```
functions:
---

The <span class="notranslate">LeftClickBlockEvent</span> class is fired when a player left-clicks
at some block.
