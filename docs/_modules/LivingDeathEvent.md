---
name: LivingDeathEvent
title: LivingDeathEvent
subtitle: When a Living Entity Dies
type: event
extends: LivingEvent
layout: module
properties:
  - name: cause
    type: string
    access: r
    description: "The cause of death. This is something like 'drown', 'lava', 'fall', etc.
    "
    examples:
      - url: LivingDeathEvent/cause.md
functions:
---

The <span class="notranslate">LivingDeathEvent</span> class is fired when an entity dies.
