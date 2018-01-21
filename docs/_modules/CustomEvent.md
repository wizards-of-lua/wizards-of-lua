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
    description: "The data value that has been sended with this event.
    See [Events.fire()](!SITE_URL!/modules/Events/#fire) for more details on this.
    "
    examples:
      - url: CustomEvent/data.md
functions:
---

The CustomEvent represents any event that has been fired from some Lua code using
[Events.fire()](/modules/Events/#fire).
