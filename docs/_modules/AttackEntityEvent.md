---
name: AttackEntityEvent
title: AttackEntityEvent
subtitle: 
type: event
extends: Event
layout: module
properties:
  - name: player
    type: '[Player](/modules/Player)'
    access: r
    description: |
        This is the [player](/modules/Player) that attacks the entity.
       
        #### Example
       
        Canceling the attack event when the player is invisible.
       
        ```lua
        Events.on('AttackEntityEvent'):call(function(event)
          if event.player.invisible then
            event.canceled = true
          end
        end)
        ```
  - name: target
    type: '[Entity](/modules/Entity)'
    access: r
    description: |
        This is the [entity](/modules/Entity) that is attacked.
       
        #### Example
       
        Canceling the attack event when the target is a zombie.
       
        ```lua
        Events.on('AttackEntityEvent'):call(function(event)
          if event.target.entityType == "zombie" then
            event.canceled = true
          end
        end)
        ```
functions:
---

The <span class="notranslate">AttackEntityEvent</span> is fired when a [player](/modules/Player)
attacks an [Entity](/modules/Entity).
