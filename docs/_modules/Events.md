---
title: The Knowlede of What is Happening
name: Events
properties:
functions:
  - name: register
    parameters: eventtypes
    results: queue
    description: "The 'register' function adds the current spell as a listener
    for events of the given event types and returns an event queue.
    The event queue will contain all events of the specified types that
    happend since the registration in order of appearance.
    "
    examples:
      - url: Events/register.md
  - name: fire
    parameters: eventtype, content
    results: nil
    description: "The 'fire' function creates an event object of the given
    event type, fills it with the given content, and 'fires' it into the
    world so that it can be received by any registered event listener.
    "
    examples:
      - url: Events/fire.md
---
{% include module-head.md %}

The 'Events' module provides access to the world's event system.

The following Minecraft event types are supported right now:
* CHAT
* WHISPER
* LEFT_CLICK
* RIGHT_CLICK
* PLAYER_JOINED
* PLAYER_LEFT
* PLAYER_SPAWNED
* ANIMATION_HAND

Aditionally you can create your own event types and use them with
the event module.

{% include module-body.md %}
