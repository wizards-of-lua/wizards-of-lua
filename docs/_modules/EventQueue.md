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
    description: "These are the [names](/modules/Event/#name) of all events
    this queue is [collecting](/modules/Events/#collect).
    "
functions:
  - name: stop
    parameters:
    results: nil
    description: "The 'stop' function disconnects this queue from the event source so that
    it will not collect any events anymore.
    "
    examples:
      - url: EventQueue/stop.md
  - name: isEmpty
    parameters:
    results: boolean
    description: "The 'isEmpty' function returns true if this queue is empty, false otherwise.
    "
    examples:
      - url: EventQueue/isEmpty.md
  - name: latest
    parameters:
    results: "[Event](/modules/Event/)"
    description: "The 'latest' function returns the newest event in this queue and discards all older events.
    If the queue [is empty](/modules/EventQueue/#isEmpty) then nil is returned.
    This is useful for update events where you are only interested in the most recent change.
    "
    examples:
      - url: EventQueue/latest.md
  - name: next
    parameters: timeout
    results: "[Event](/modules/Event/)"
    description: "The 'next' function returns the next event in this queue, if any.
    This function blocks until an event is available or the given timeout (measured in game ticks) is reached.
    If no timeout is specified, this function blocks forever.
    "
    examples:
      - url: EventQueue/next.md
---

The <span class="notranslate">EventQueue</span> class collects [events](/modules/Event/) when it is connected to the event source.
