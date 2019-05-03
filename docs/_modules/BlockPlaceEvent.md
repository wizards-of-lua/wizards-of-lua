---
name: BlockPlaceEvent
title: BlockPlaceEvent
subtitle: When a Player Places a Block
type: event
extends: BlockEvent
layout: module
properties:
  - name: hand
    type: 'string'
    access: r
    description: |
        The hand the player used to place the block. Can be 'MAIN_HAND' or 'OFF_HAND'.
  - name: placedAgainst
    type: '[Block](/modules/Block)'
    access: r
    description: |
        The block against which the new block was placed. Unfortunately the NBT of the block
        placedAgainst is unavailable in this event.
       
        #### Example
       
        Transform all torches that are placed against a redstone block into redstone torches.
       
        ```lua
        local queue = Events.collect("BlockPlaceEvent")
        while true do
          local event = queue:next()
          if event.block.name == 'torch' and event.placedAgainst.name == 'redstone_block' then
            spell.pos = event.pos
            spell.block = Blocks.get('redstone_torch'):withData(event.block.data)
          end
        end
        ```
  - name: player
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The player that triggered this event.
  - name: replacedBlock
    type: '[Block](/modules/Block)'
    access: r
    description: |
        The block that is replaced by this event.
functions:
---

The <span class="notranslate">BlockPlaceEvent</span> class is fired when a player places a
[block](/modules/Block).
