---
name: Events
subtitle: Knowing What Happened
type: module
layout: module
properties:
functions:
  - name: connect
    parameters: string...
    results: "[EventQueue](!SITE_URL!/modules/EventQueue/)"
    description: "The 'connect' function creates and returns an [EventQueue](!SITE_URL!/modules/EventQueue/)
    that is connected to the event source.
    It will collect all occuring [events](!SITE_URL!/modules/Event/) of the given kind(s).
    "
    examples:
      - url: Events/connect.md
  - name: fire
    parameters: string, object
    results: nil
    description: "The 'fire' function posts a new [CustomEvent](!SITE_URL!/modules/CustomEvent/)
    with the given name and the optional given content data.
    "
    examples:
      - url: Events/fire.md
---

The Events module provides functions for accessing and firing [Events](/modules/Event/).
