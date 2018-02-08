---
name: Event
subtitle: The Event Base Class
type: event
layout: module
properties:
  - name: name
    type: string
    access: r
    description: "The name of this kind of event.
    Use this name to [connect an event queue](/modules/Events/#connect) to the event source for events of this kind.
    "
functions:
---

The <span class="notranslate">Event</span> class represents a notification about something that happend in the world.
