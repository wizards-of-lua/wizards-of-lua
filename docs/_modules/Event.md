---
name: Event
subtitle: The Event Base Class
type: event
layout: module
properties:
  - name: cancelable
    type: boolean
    access: r
    description: |
        Whether the event can be [canceled](#canceled), for instance a [BlockPlaceEvent](/modules/BlockPlaceEvent) is *cancelable*, but a [SwingArmEvent](/modules/SwingArmEvent) is not.
        Events can only be canceled in an [event listener](/modules/Events#subscribe).
        If *cancelable* is <span class="notranslate">*false*</span> then setting [canceled](#canceled) results in an error.
  - name: canceled
    type: boolean
    access: r/w
    description: |
        Whether the event is canceled.
        A canceled event is not passed to any other event handlers and does not affect the world.

        #### Example
        Canceling all BlockPlaceEvents.
        ```lua
        Events.on('BlockPlaceEvent'):call(function(event)
            event.canceled = true
        end)
        ```
  - name: name
    type: string
    access: r
    description: "The name of this kind of event.
    Use this name to [connect an event queue](/modules/Events/#connect) to the event source for events of this kind.
    "
functions:
---

The <span class="notranslate">Event</span> class represents a notification about something that happend in the world.
