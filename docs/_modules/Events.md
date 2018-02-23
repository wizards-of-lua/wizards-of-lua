---
name: Events
subtitle: Knowing What Happened
type: module
layout: module
properties:
functions:
  - name: connect
    parameters: string...
    results: "[EventQueue](/modules/EventQueue/)"
    description: "The 'connect' function creates and returns an [EventQueue](/modules/EventQueue/)
    that is connected to the event source.
    It will collect all occuring [events](/modules/Event/) of the given kind(s).
    "
    examples:
      - url: Events/connect.md
  - name: fire
    parameters: string, object
    results: nil
    description: "The 'fire' function posts a new [CustomEvent](/modules/CustomEvent/)
    with the given name and the optional given content data.
    "
    examples:
      - url: Events/fire.md
---

The <span class="notranslate">Events</span> module provides functions for accessing and firing [Events](/modules/Event/).
