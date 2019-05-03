---
name: BlockBreakEvent
title: BlockBreakEvent
subtitle: When a Player Breaks a Block
type: event
extends: BlockEvent
layout: module
properties:
  - name: experience
    type: 'number (int)'
    access: r/w
    description: |
        This is the amount of experience to drop by the block, if the event won't be canceled.
       
        #### Example
       
        Drop a number of diamonds equal to the amount of experience a player gains through mining
        coal ore, redstone ore, etc. Note that you don't get experience from breaking blocks in
        creative mode.
       
        ```lua
        local queue = Events.collect("BlockBreakEvent")
        while true do
          local event = queue:next()
          spell.pos = event.pos
          if event.experience > 0 then
            spell:dropItem(Items.get('diamond', event.experience))
          end
        end
        ```
       
        #### Example
       
        Drop 20 experience points when a players breaks a grass block.
       
        ```lua
        Events.on("BlockBreakEvent"):call(function(event)
          if event.block.name == "grass" then
            event.experience = 20
          end
        end)
        ```
  - name: player
    type: '[Player](/modules/Player)'
    access: r
    description: |
        This is the [player](/modules/Player) who broke the block.
functions:
---

The <span class="notranslate">BlockBreakEvent</span> is fired when an Block is about to be broken
by a player.

Canceling this event will prevent the Block from being broken.
