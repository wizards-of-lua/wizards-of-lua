---
name: EntityJoinWorldEvent
title: EntityJoinWorldEvent
subtitle: When an Entity Enters the World
type: event
extends: EntityEvent
layout: module
properties:
  - name: world
    type: '[World](/modules/World)'
    access: r
    description: |
        This is the [world](/modules/World) in which this entity is going to join.
functions:
---

The <span class="notranslate">EntityJoinWorldEvent</span> is fired when an entity joins the
world. This happens e.g. when an entity is spawned and when a chunk with existing entities is
loaded into the server's memory.
