---
name: LeftClickBlockEvent
title: LeftClickBlockEvent
subtitle: When a Player left-clicks on a Block
type: event
extends: PlayerInteractEvent
layout: module
properties:
  - name: hitVec
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r
    description: "The exact position the player clicked at.
    "
functions:
---

The LeftClickBlockEvent class is fired when a player left-clicks at some block.
