---
name: BlockPlaceEvent
title: BlockPlaceEvent
subtitle: When a Player Places a Block
type: event
extends: BlockEvent
layout: module
properties:
  - name: hand
    type: string
    access: r
    description: "The hand the player used to place the block.
    Can be 'MAIN_HAND' or 'OFF_HAND'.
    "
  - name: placedAgainst
    type: "[Block](!SITE_URL!/modules/Block/)"
    access: r
    description: "The block against which the new block was placed.
    Unfortunately the NBT of the block placedAgainst is unavailable.
    "
    examples:
      - url: BlockPlaceEvent/placedAgainst.md
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that triggered this event.
    "
functions:
---

The <span class="notranslate">BlockPlaceEvent</span> class is fired when a player places a block.
