---
name: PlayerLoggedInEvent
title: PlayerLoggedInEvent
subtitle: When a Player Joins the World
type: event
extends: Event
layout: module
properties:
  - name: 'player'
    type: '[Player](/modules/Player)'
    access: r
    description: |
        The player who joined the world.
functions:
---

The <span class="notranslate">PlayerLoggedInEvent</span> is fired whenever a
[Player](/modules/Player) joins the world (server).
