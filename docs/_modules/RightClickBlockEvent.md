---
name: RightClickBlockEvent
title: RightClickBlockEvent
subtitle: When a Player right-clicks on a Block
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

The RightClickBlockEvent class is fired when a player right-clicks at some block.
