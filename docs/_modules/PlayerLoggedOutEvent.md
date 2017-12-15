---
name: PlayerLoggedOutEvent
title: PlayerLoggedOutEvent
subtitle: When a Player leaves the World
type: event
extends: Event
layout: module
properties:  
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that left the world.
    "
functions:
---

The PlayerLoggedOutEvent is fired whenever a [Player](/modules/Player) leaves the
world (server).
