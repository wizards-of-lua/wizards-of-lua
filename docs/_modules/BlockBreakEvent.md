---
name: BlockBreakEvent
title: BlockBreakEvent
subtitle: When a [Player](!SITE_URL!/modules/Player/) breaks a [Block](!SITE_URL!/modules/Block/)
type: event
extends: BlockEvent
layout: module
properties:
  - name: experience
    type: number (integer)
    access: r
    description: "The experience dropped by the block.
    "
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that triggered this event.
    "
functions:
---

The BlockBreakEvent class is fired when a player breaks a block.
