---
name: RightClickBlockEvent
title: RightClickBlockEvent
subtitle: When a Player Right-Clicks on a Block
type: event
extends: PlayerInteractEvent
layout: module
properties:
  - name: 'hitVec'
    type: '[Vec3](/modules/Vec3)'
    access: r
    description: |
        The exact position the player clicked at.
functions:
---

The <span class="notranslate">RightClickBlockEvent</span> class is fired when a player
right-clicks at some block.
