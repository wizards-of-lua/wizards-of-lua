---
name: Time
title: Time
subtitle: Accessing the Time
type: module
layout: module
properties:
  - name: 'allowance'
    type: 'number (long)'
    access: r
    description: |
        The allowance is the number of lua ticks that are left before the spell or event listener is
        broken or sent to sleep, depending on [autosleep](#autosleep).
  - name: 'autosleep'
    type: 'boolean'
    access: r/w
    description: |
        The autosleep value defines whether the current spell should go to sleep automatically when its
        allowance is exceeded.
       
        If this is set to false, the spell will never go to sleep automatically, but instead will be
        broken when its allowance reaches zero. Default is true normally, but in an [event
        interceptor](/modules/Events#intercept) 'autosleep' is always false and can't be changed.
  - name: 'gametime'
    type: 'number (long)'
    access: r
    description: |
        The gametime is the number of game ticks that have passed since the world has been created.
  - name: 'luatime'
    type: 'number (long)'
    access: r
    description: |
        The luatime is the number of lua ticks that the current spell has worked since it has been
        casted. This includes lua ticks of event listeners.
  - name: 'realtime'
    type: 'number (long)'
    access: r
    description: |
        The realtime is the number of milliseconds that have passed since January 1st, 1970.
functions:
  - name: 'getDate'
    parameters: pattern
    results: 'string'
    description: |
        Returns a string with the current real date and time. If you want you can change the format by
        providing an optional format string.
       
        #### Example
       
        Printing the current date and time in ISO date-time format, such as '2011-12-03T10:15:30'.
       
        ```lua
        print(Time.getDate())
        ```
       
        #### Example
       
        Printing the current date and time with a custom format, such as '03 Oct, 2017 - 10:15:29'.
       
        ```lua
        print(Time.getDate("dd MMM, yyyy - HH:mm:ss"))
        ```
  - name: 'sleep'
    parameters: ticks
    results: '[nil](/modules/nil)'
    description: |
        Forces the current spell to sleep for the given number of game ticks.
       
        If the number is 0, the spell won't sleep. If the number is negative, this function will issue
        an error. If the number is nil, the spell might go to sleep or not. This depends on the number
        of lua ticks that are already consumed by this spell.
       
        The rule is as follows: the spell will be sent to sleep if the spell's allowance falls below
        the half value of the spell's initial allowance.
       
        #### Example
       
        Sending the current spell to sleep for 100 game ticks, which are approximately 5 seconds.
       
        ```lua
        Time.sleep(100)
        ```
       
        Since <span class="notranslate">'sleep'</span> is a widely used function, there is a shortcut
        for it.
       
        ```lua
        sleep(100)
        ```
---

The <span class="notranslate">Time</span> module provides access to time related properties of
the active Spell's world.
