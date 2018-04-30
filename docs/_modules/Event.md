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
        Events can only be canceled in an [event interceptor](/modules/Events#intercept).
        If *cancelable* is <span class="notranslate">*false*</span> then setting [canceled](#canceled) results in an error.
  - name: canceled
    type: boolean
    access: r/w
    description: |
        Whether the event is canceled.
        A canceled event is not passed to any other [event interceptor](/modules/Events#intercept) or [EventQueues](/modules/EventQueue) and does not affect the world.

        Please note that an event can only be canceled by [event interceptors](/modules/Events#intercept), since those are called *before* the actual event is handled by Minecraft.
        Therefore an event can't be canceled if it is already collected by an [EventQueues](/modules/EventQueue), because this happens after the event has been handled by Minecraft.

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
    Use this name to [connect an event queue](/modules/Events/#collect) to the event source for events of this kind.
    "
functions:
---

The <span class="notranslate">Event</span> class represents a notification about something that happend in the world.
