---
name: Runtime
subtitle: Controlling the Runtime Environment
layout: module
properties:
functions:
  - name: getAllowance
    parameters:
    results: number
    description: "Returns the number of lua ticks the active spell has left before
    a sleep period must be taken."
  - name: getRealtime
    parameters:
    results: number
    description: "Returns the number of milliseconds that passed since
    January 1st, 1970."
  - name: getGametime
    parameters:
    results: number
    description: "Returns the number of game ticks that have passed since the
    world has been created."
  - name: getLuatime
    parameters:
    results: number
    description: "Returns the number of lua ticks that have been activly consumed
    by the current spell since it has been casted."
  - name: getRealDateTime
    parameters: string
    results: string
    description: "Returns a string with the current real date and time.
    If you want you can change the format by providing a format string."
  - name: setAutoSleep
    parameters: boolean
    results: nil
    description: "Set this to true if the spell should go to sleep automatically
    when its allowance is exceeded. If this is set to false, the spell will
    never go to sleep automatically, but instead will be broken when its allowance
    falls below zero."
  - name: sleep
    parameters: number
    results: nil
    description: "Forces the current spell to sleep for the given amount of
    game ticks."
---

The Runtime module provides access to runtime properties of the active Spell's world.
