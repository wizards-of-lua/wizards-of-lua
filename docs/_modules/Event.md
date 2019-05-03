---
name: Event
title: Event
subtitle: The Event Base Class
type: event
extends: Object
layout: module
properties:
  - name: cancelable
    type: 'boolean'
    access: r
    description: |
        The 'cancelable' property can be used to detect dynamically whether this event instance can
        be [canceled](#canceled) by calling
       
        ```lua
        event.canceled = true
        ```
       
        In general, this is determined by the event class.
       
        For instance, a [BlockPlaceEvent](/modules/BlockPlaceEvent) is *cancelable*, but a
        [SwingArmEvent](/modules/SwingArmEvent) is not.
       
        If *cancelable* is <span class="notranslate">*false*</span>, then setting
        [canceled](#canceled) results in an error.
       
        Please note, an event can only be canceled during [event
        interception](/modules/Events#intercept).
  - name: canceled
    type: 'boolean'
    access: r/w
    description: |
        The 'canceled' property can be used to define whether this event should be canceled.
       
        If *cancelable* is <span class="notranslate">*false*</span>, then setting
        [canceled](#canceled) results in an error.
       
        Please note, an event can only be canceled during [event
        interception](/modules/Events#intercept).
       
        #### Example
       
        Canceling all chat messages from player 'mickkay'.
       
        ```lua
        Events.on('ChatEvent'):call(function(event)
          if event.player.name == 'mickkay' then
             event.canceled = true
          end
        end)
        ```
  - name: name
    type: 'string'
    access: r
    description: |
        The name of this kind of event. Use this name to [connect an event
        queue](/modules/Events/#collect) to the event source for events of this kind.
functions:
---

The <span class="notranslate">Event</span> class represents a notification about something that
happend in the world.
