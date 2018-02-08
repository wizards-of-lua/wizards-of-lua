---
name: PlayerLoggedInEvent
title: PlayerLoggedInEvent
subtitle: When a Player Joins the World
type: event
extends: Event
layout: module
properties:  
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that joined the world.
    "
functions:
---

The <span class="notranslate">PlayerLoggedInEvent</span> is fired whenever a [Player](/modules/Player) joins the
world (server).
