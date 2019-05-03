---
name: EventQueue
title: EventQueue
subtitle: Collecting Events
type: class
extends: Object
layout: module
properties:
  - name: names
    type: 'table'
    access: r
    description: |
        These are the [names](/modules/Event#name) of all events this queue is
        [collecting](/modules/Events#collect).
functions:
  - name: isEmpty
    parameters: 
    results: 'boolean'
    description: |
        The 'isEmpty' function returns true if this queue is empty, false otherwise.
       
        #### Example
       
        Busy-waiting for a chat event and printing the message when it occurs.
       
        ```lua
        local queue = Events.collect("ChatEvent")
        while queue:isEmpty() do
          sleep(20)
          print("still waiting...")
        end
        local event = queue:next(0)
        print("You said "..event.message)
        ```
  - name: latest
    parameters: 
    results: '[Event](/modules/Event)'
    description: |
        The 'latest' function returns the newest event in this queue and discards all older events.
        If the queue [is empty](/modules/EventQueue#isEmpty) then nil is returned. This is useful for
        update events where you are only interested in the most recent change.
       
        #### Example
       
        Echo the last chat message every 5 seconds.
       
        ```lua
        local queue = Events.collect("ChatEvent")
        while true do
          local event = queue:latest()
          if event ~= nil then
            spell:execute("say %s", event.message)
          end
          sleep(5 * 20)
        end
        ```
  - name: next
    parameters: timeout
    results: '[Event](/modules/Event)'
    description: |
        The 'next' function returns the next event in this queue, if any. This function blocks until
        an event is available or the given timeout (measured in game ticks) is reached. If no timeout
        is specified, this function blocks forever.
       
        #### Example
       
        Echoing all chat messages.
       
        ```lua
        local queue = Events.collect("ChatEvent")
        while true do
          local event = queue:next()
          spell:execute("say %s", event.message)
        end
        ```
  - name: stop
    parameters: 
    results: 'nil'
    description: |
        The 'stop' function stops collecting events into this queue.
       
        #### Example
       
        Collecting chat events and stopping it after the first event occurs.
       
        ```lua
        local queue = Events.collect("ChatEvent")
        local event = queue:next()
        print(str(event))
        queue:stop()
        ```
---

The <span class="notranslate">EventQueue</span> class collects [events](/modules/Event) when it
is connected to the event source.
