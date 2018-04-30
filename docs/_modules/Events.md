---
name: Events
subtitle: Knowing What Happened
type: module
layout: module
properties:
functions:
  - name: collect
    parameters: string...
    results: "[EventQueue](/modules/EventQueue/)"
    description: "The 'collect' function creates and returns an [EventQueue](/modules/EventQueue/)
    that is connected to the event source.
    It will collect all occuring [events](/modules/Event/) of the given kind(s).
    "
    examples:
      - url: Events/collect.md
  - name: fire
    parameters: string, object
    results: nil
    description: "The 'fire' function posts a new [CustomEvent](/modules/CustomEvent/)
    with the given name and the optional given content data.
    "
    examples:
      - url: Events/fire.md
  - name: "on"
    parameters: eventNames...
    results: table
    description: |
        Returns a table containing the specified event names and a reference to [intercept](#intercept).
        This can be used as a short cut for [intercept](#intercept).
        #### Example
        Subscribing for chat events.
        ```lua
        local interceptor = Events.on('ChatEvent'):call(
          function(event)
            print(str(event))
          end
        )
        ```
  - name: intercept
    parameters: eventNames, eventListener
    results: "[EventInterceptor](/modules/EventInterceptor)"
    description: |
        Connects the specified event interceptor to events with the specified names.
        It will be called immediately when an event occurs, this allows events to be canceled.
        Events interceptors do not support sleeping; therefor, [autosleep](/modules/Time#autosleep) is disabled and sleeping manually is treated as an illegal operation.
        As long as a spell has active event interceptors it doesn't terminate so make sure to stop all event interceptors that are no longer needed.
        In order to cancel the interceptor an [EventInterceptor](/modules/EventInterceptor) is returned.

        #### Example
        Intercepting chat events.
        ```lua
        local interceptor = Events.intercept({'ChatEvent'},
          function(event)
            print(str(event))
          end
        )
        ```

        **Note**: Be careful when modifying variables that are used in the main program.
        If [autosleep](/modules/Time#autosleep) is enabled the main program can fall asleep eventually at any time and a variable might be modified in an awkward situation.
        For instance, the following program fails due to indexing a nil value in line 8 despite the nil check in line 6.
        In this example there is an explicit sleep in line 7, but that sleep could just as well be caused by [autosleep](/modules/Time#autosleep).
        ```lua
        local abc = 'abc'
        local interceptor = Events.intercept({'my-event'},
          function(event)
            abc = nil
          end
        )
        spell:execute("lua Events.fire('my-event')")
        if abc ~= nil then
          sleep(1)
          print(abc:len())
          end
        interceptor:stop()
        ```
---

The <span class="notranslate">Events</span> module provides functions for accessing and firing [Events](/modules/Event/).
