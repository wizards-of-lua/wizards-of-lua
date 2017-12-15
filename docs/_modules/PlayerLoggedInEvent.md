---
name: PlayerLoggedInEvent
title: PlayerLoggedInEvent
subtitle: When a Player joins the World
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

The PlayerLoggedInEvent is fired whenever a [Player](/modules/Player) joins the
world (server).
