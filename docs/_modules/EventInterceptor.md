---
name: EventInterceptor
title: EventInterceptor
subtitle:
type: class
layout: module
properties:
functions:
  - name: stop
    results: nil
    description: |
        The 'stop' function terminates this interceptor so that the corresponding function is no longer called for new events.
        #### Example
        Intercepting the next chat event and stopping immediately once the first one occurs.
        ```lua
        local i
        i = Events.on('ChatEvent'):call(function(event)
            print(str(event))
            i:unsubscribe()
        end)
        ```
---

An <span class="notranslate">EventInterceptor</span> represents an interceptor of events. A interceptor can be obtained through [Events.intercept()](/modules/Events#intercept).
