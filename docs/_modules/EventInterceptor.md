---
name: EventInterceptor
title: EventInterceptor
subtitle: Intercepting Events
type: class
extends: Object
layout: module
properties:
functions:
  - name: stop
    parameters: 
    results: 'nil'
    description: |
        The 'stop' function terminates this interceptor so that the corresponding function is no
        longer called for new events.
       
        #### Example
       
        Intercepting the next chat event and stopping immediately once the first one occurs.
       
        ```lua
        local i
        i = Events.on('ChatEvent'):call(function(event)
          print(event.player.name, event.message)
          i:unsubscribe()
        end)
        ```
---

An <span class="notranslate">EventInterceptor</span> represents an interceptor of events.

An interceptor can be obtained through [Events.intercept()](/modules/Events#intercept) and
[Events.on(...):call()](/modules/Events#on).

In contrast to an [EventQueue](/modules/EventQueue), an event interceptor is capable of event
mutation and event cancellation since it is called 'in-line' with the event occurrence.
