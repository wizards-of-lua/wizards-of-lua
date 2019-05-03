---
name: PlayerInteractEvent
title: PlayerInteractEvent
subtitle: 
type: event
extends: Event
layout: module
properties:
  - name: 'face'
    type: 'string'
    access: r
    description: |
        The face of the block that was clicked at. Can be one of 'up', 'down', 'north', 'east',
        'south', and 'west'.
  - name: 'hand'
    type: 'string'
    access: r
    description: |
        The hand the player used to hit the block. Can be 'MAIN_HAND' or 'OFF_HAND'.
  - name: 'item'
    type: '[Item](/modules/Item)'
    access: r
    description: |
        The item in the player's hand.
  - name: 'player'
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The player that triggered this event.
  - name: 'pos'
    type: '[Vec3](/modules/Vec3)'
    access: r
    description: |
        The block's position.
functions:
---

The <span class="notranslate">PlayerInteractEvent</span> is the base class of
[LeftClickBlockEvent](/modules/LeftClickBlockEvent/) and
[RightClickBlockEvent](/modules/RightClickBlockEvent/).
