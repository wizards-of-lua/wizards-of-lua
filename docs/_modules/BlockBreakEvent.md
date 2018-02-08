---
name: BlockBreakEvent
title: BlockBreakEvent
subtitle: When a Player Breaks a Block
type: event
extends: BlockEvent
layout: module
properties:
  - name: experience
    type: number
    access: r
    description: "The experience dropped by the block.
    "
    examples:
      - url: BlockBreakEvent/experience.md
  - name: player
    type: "[Player](!SITE_URL!/modules/Player/)"
    access: r
    description: "The player that triggered this event.
    "
functions:
---

The <span class="notranslate">BlockBreakEvent</span> class is fired when a player breaks a block.
