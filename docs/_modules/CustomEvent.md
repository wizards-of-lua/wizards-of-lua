---
name: CustomEvent
title: CustomEvent
subtitle:
type: event
extends: Event
layout: module
properties:  
  - name: data
    type: any
    access: r
    description: "The data value that has been sent with this event.
    See [Events.fire()](!SITE_URL!/modules/Events/#fire) for more details on this.
    "
    examples:
      - url: CustomEvent/data.md
functions:
---

The <span class="notranslate">CustomEvent</span> represents any event that has been fired from some Lua code using
[Events.fire()](/modules/Events/#fire).
