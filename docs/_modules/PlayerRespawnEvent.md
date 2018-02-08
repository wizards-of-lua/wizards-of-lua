---
name: PlayerRespawnEvent
title: PlayerRespawnEvent
subtitle: When a Player Comes into Existence
type: event
extends: Event
layout: module
properties:  
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that is born.
    "
functions:
---

The <span class="notranslate">PlayerRespawnEvent</span> is fired whenever a [Player](/modules/Player) is born.
