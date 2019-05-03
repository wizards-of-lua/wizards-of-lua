---
name: PlayerEntityInteractEvent
title: PlayerEntityInteractEvent
subtitle: When a Player Right-Clicks on an Entity
type: event
extends: PlayerInteractEvent
layout: module
properties:
  - name: 'target'
    type: '[Entity](/modules/Entity)'
    access: r
    description: |
        The target entity.
functions:
---

The <span class="notranslate">PlayerEntityInteractEvent</span> class is fired when a player
right-clicks somewhere an [entity](/modules/Entity)
