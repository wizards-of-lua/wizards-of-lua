---
name: EventSubscription
title: EventSubscription
subtitle:
type: class
layout: module
properties:
functions:
  - name: unsubscribe
    results: nil
    description: |
        The 'unsubscribe' function cancels this subscription so that the corresponding function is no longer called to handle new events.
        #### Example
        Subscribing for the next chat event and unsubscribing once the first one occurs.
        ```lua
        local subscription
        subscription = Events.on('ChatEvent'):call(function(event)
            print(str(event))
            subscription:unsubscribe()
        end)
        ```
---

An <span class="notranslate">EventSubscription</span> represents a subscription of an event handler. A subscription can be obtained through [Events.subscribe()](/modules/Events#subscribe).
