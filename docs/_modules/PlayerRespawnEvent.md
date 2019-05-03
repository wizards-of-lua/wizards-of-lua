---
name: PlayerRespawnEvent
title: PlayerRespawnEvent
subtitle: When a Player is Reborn
type: event
extends: Event
layout: module
properties:
  - name: 'endConquered'
    type: 'boolean'
    access: r
    description: |
        This is <span class="notranslate">true</span> if this respawn was because the player
        conquered the end.
  - name: 'player'
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The player who respawned in the world.
functions:
---

The <span class="notranslate">PlayerRespawnEvent</span> is fired whenever a
[Player](/modules/Player) is reborn.
