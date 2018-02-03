---
name: BlockEvent
title: BlockEvent
subtitle:
type: event
extends: Event
layout: module
properties:
  - name: block
    type: "[Block](!SITE_URL!/modules/Block/)"
    access: r
    description: "The block this event is about. For instance the newly placed block if the event is a [BlockPlaceEvent](/modules/BlockPlaceEvent/).
    "
  - name: pos
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r
    description: "The block's position.
    "
functions:
---

The BlockEvent is the base class of [BlockBreakEvent](/modules/BlockBreakEvent/)
and [BlockPlaceEvent](/modules/BlockPlaceEvent/).
