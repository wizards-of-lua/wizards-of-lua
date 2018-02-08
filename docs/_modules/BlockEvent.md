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
    description: "This is the block this event is about.
    Please note that the [nbt value](!SITE_URL!/modules/Block/#nbt) value of the block is only available for a [BlockPlaceEvent](!SITE_URL!/modules/BlockPlaceEvent/).
    "
  - name: pos
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r
    description: "The block's position.
    "
functions:
---

The <span class="notranslate">BlockEvent</span> is the base class of [BlockBreakEvent](/modules/BlockBreakEvent/)
and [BlockPlaceEvent](/modules/BlockPlaceEvent/).
