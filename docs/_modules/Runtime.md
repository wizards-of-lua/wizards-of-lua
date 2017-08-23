---
title: Controlling the Spell's Runtime Environment
name: Runtime
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
    parameters:
    results: boolean
    description: "Set this to true if the spell should go to sleep automatically
    when its allowance is exceeded. If this is set to false, the spell will
    never go to sleep automatically, but instead will be broken when its allowance
    falls below zero."
  - name: sleep
    parameters:
    results: number
    description: "Forces the current spell to sleep for the given amount of
    game ticks."
---
{% include module-head.md %}

The Runtime module provides access to runtime properties of the active Spell's world.

{% include module-body.md %}
