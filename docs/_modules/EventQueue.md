---
name: EventQueue
title: EventQueue
subtitle: Collecting Events
type: class
layout: module
properties:
  - name: names
    type: table
    access: r
    description: "These are the [names](/modules/Event/#name) of those events
    this queue is [connected](/modules/Events/#connect) to.
    "
functions:
  - name: disconnect
    parameters:
    results: nil
    description: "The 'disconnect' function disconnects this queue from the event source so that
    it will not collect any events anymore.
    "
    examples:
      - url: EventQueue/disconnect.md
  - name: next
    parameters: timeout
    results: "[Event](!SITE_URL!/modules/Event/)"
    description: "The 'next' function returns the next event in this queue, if any.
    This function blocks until an event is available or the given timeout (measured in game ticks) is reached.
    If no timeout is specified, this function blocks forever.
    "
    examples:
      - url: EventQueue/next.md
  - name: isEmpty
    parameters:
    results: boolean
    description: "The 'isEmpty' function returns true if this queue is empty, false otherwise.
    "
    examples:
      - url: EventQueue/isEmpty.md
---

The EventQueue class collects [events](/modules/Event/) when it is connected to the event source.
