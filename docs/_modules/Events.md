---
name: Events
title: Events
subtitle: Knowing What Happened
type: module
layout: module
properties:
functions:
  - name: 'collect'
    parameters: eventName...
    results: '[EventQueue](/modules/EventQueue)'
    description: |
        The 'collect' function creates an [EventQueue](/modules/EventQueue/) that collects all
        [Event](/modules/Event) occurrences of the specified kind(s).
       
        #### Example
       
        Echoing all chat messages.
       
        ```lua
        local queue = Events.collect("ChatEvent")
        while true do
          local event = queue:next()
          spell:execute("say %s", event.message)
        end
        ```
       
        #### Example
       
        Posting the position of all block-click events into the chat.
       
        ```lua
        local queue=Events.collect("LeftClickBlockEvent","RightClickBlockEvent")
        while true do
          local event = queue:next()
          spell:execute("say %s at %s", event.name, event.pos)
        end
        ```
  - name: 'fire'
    parameters: eventName, data
    results: 'nil'
    description: |
        The 'fire' function posts a new [CustomEvent](/modules/CustomEvent/) with the given name and
        the optional given content data.
       
        #### Example
       
        Firing a custom event without any data.
       
        ```lua
        Events.fire("my-event")
        ```
       
        #### Example
       
        Registering an event intereptor for a custom event that prints the event data.
       
        ```lua
        Events.on("my-event"):call(function(event)
          print(str(event.data))
        end)
        ```
       
        Firing a custom event with some data.
       
        ```lua
        local data = spell.block
        Events.fire("my-event", data)
        ```
  - name: 'intercept'
    parameters: eventNames, eventHandler
    results: '[EventInterceptor](/modules/EventInterceptor)'
    description: |
        Creates an event interceptor for [Events](/module/Event) with the specified names.
       
        The interceptor will be called immediately when an event occurs, which allows events to be
        modified and [canceled](/modules/Event#canceled).
       
        Event interceptors do not support [sleeping](/modules/Time#sleep) - therefor,
        [autosleep](/modules/Time#autosleep) is disabled and manual sleeping is treated as an illegal
        operation.
       
        As long as a [Spell](/modules/Spell) has any active event interceptors it will not terminate by
        itself, so make sure to [stop](/modules/EventInterceptor#stop) each event interceptor that is
        no longer needed.
       
        #### Example
       
        Intercepting chat events.
       
        ```lua
        local interceptor =
        Events.intercept({'ChatEvent'}, function(event)
          print(str(event))
        end )
        ```
       
        #### *Warning: Beware of possible race conditions!*
       
        Be careful, when accessing variables that are used both by the main program as well as by the
        event interceptor.
       
        If [autosleep](/modules/Time#autosleep) is enabled, the main program can fall asleep eventually
        at any time, which allows that a variable might be modified in an awkward situation.
       
        For instance, the following program fails due to indexing a nil value in line 10 despite the
        nil check in line 8.
       
        In this example there is an explicit sleep in line 9, but that sleep could just as well be
        caused by [autosleep](/modules/Time#autosleep).
       
        ```lua
        local abc = 'abc'
        local interceptor = Events.intercept({'my-event'}, function(event)
          abc = nil
        end)
        spell:execute([[lua
          Events.fire('my-event')
        ]])
        if abc ~= nil then
          sleep(1)
          print(abc:len())
        end
        interceptor:stop()
        ```
  - name: 'on'
    parameters: eventName...
    results: 'table'
    description: |
        Returns a table containing the specified event names and a reference to
        [intercept](#intercept). This can be used as shorthand for [intercept](#intercept).
       
        #### Example
       
        Subscribing for chat events and printing the messages.
       
        ```lua
        local interceptor = Events.on('ChatEvent'):call( function(event)
          print(event.message)
        end)
        ```
---

The <span class="notranslate">Events</span> module provides functions for accessing and firing
[Events](/modules/Event/).
