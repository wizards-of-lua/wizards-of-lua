---
name: Time
subtitle: Accessing the Time
layout: module
properties:
  - name: autosleep
    type: boolean
    access: r/w
    description: "The autosleep value defines whether the current spell
    should go to sleep automatically when its allowance is exceeded.
    If this is set to false, the spell will never go to sleep automatically,
    but instead will be broken when its allowance falls below zero.
    Default is true.
    "
  - name: allowance
    type: number
    access: r
    description: "The allowance is the number of lua ticks that are left before
    the active spell must sleep for at least one game tick.
    "
  - name: realtime
    type: number
    access: r
    description: "The realtime is the number of milliseconds that have passed since
    January 1st, 1970.
    "
  - name: gametime
    type: number
    access: r
    description: "The gametime is the number of game ticks that have passed since the
    world has been created.
    "
  - name: luatime
    type: number
    access: r
    description: "The luatime is the number of lua ticks that the current spell
    has worked since it has been casted.
    "
functions:
  - name: getDate
    parameters: string
    results: string
    description: "Returns a string with the current real date and time.
    If you want you can change the format by providing an optional format string."
    examples:
      - url: Time/getDate.md
  - name: sleep
    parameters: number
    results: nil
    description: "Forces the current spell to sleep for the given amount of
    game ticks."
    examples:
      - url: Time/sleep.md
---

The Time module provides access to time related properties of the active Spell's world.
