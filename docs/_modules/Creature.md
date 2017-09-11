---
name: Creature
subtitle: Controlling Creatures
layout: module
extends: Entity
properties:
  - name: ai
    type: boolean
    access: r/w
    description: "The 'ai' property defines if this creature is controlled by its artificial intelligence (AI).
    It set to false this creature becomes dumb and just stands around. It even won't react to physical forces. 
    "
functions:
---

The Creature class represents a specific creature in the world.
