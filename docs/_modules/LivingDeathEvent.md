---
name: LivingDeathEvent
title: LivingDeathEvent
subtitle: When a Living Entity Dies
type: event
extends: LivingEvent
layout: module
properties:
  - name: 'cause'
    type: 'string'
    access: r
    description: |
        The cause of death. This is something like 'drown', 'lava', 'fall', etc.
       
        #### Example
       
        Rewarding a player who died in lava with a brand new lava bucket.
       
        ```lua
        local causes = {}
        local queue = Events.collect("LivingDeathEvent","PlayerRespawnEvent")
        while true do
          local event = queue:next()
          if event.name == "LivingDeathEvent" and type(event.entity)=="Player" then
            causes[event.entity.name] = event.cause
          elseif event.name == "PlayerRespawnEvent" then
            if causes[event.player.name]=="lava" then
              spell:execute("/give %s minecraft:lava_bucket", event.player.name)
            end
          end
        end
        ```
functions:
---

The <span class="notranslate">LivingDeathEvent</span> class is fired when an entity dies.
