---
name: CustomEvent
title: CustomEvent
subtitle: 
type: event
extends: Event
layout: module
properties:
  - name: data
    type: '[any](/modules/any)'
    access: r
    description: |
        The data value that has been sent with this event. See [Events.fire()](/modules/Events/#fire)
        for more details on this.
       
        #### Example
       
        Firing a custom event with some complex data.
       
        ```lua
        local data = {pos=spell.pos, time=Time.gametime}
        Events.fire("my-event", data)
        ```
       
        #### Example
       
        Accessing the data of a custom event.
       
        ```lua
        local q = Events.collect("my-event")
        local event = q:next()
        print("event.data", str(event.data))
        ```
functions:
---

The <span class="notranslate">CustomEvent</span> represents any event that has been fired from
some Lua code using [Events.fire()](/modules/Events/#fire), for example:

```lua
Events.fire("my-event", {someKey="some data"})
```
