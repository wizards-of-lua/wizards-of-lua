---
name: Mob
subtitle: Mobile Creatures
type: class
extends: Entity
layout: module
properties:
  - name: ai
    type: boolean
    access: r/w
    description: "The 'ai' property defines if this mobile creature is currently controlled
    by its artificial intelligence (AI).
    If set to false this creature becomes dumb and just stands around. It even won't react to physical forces.
    Default is true.
    "
functions:
---

The Mob class represents mobile creatures that are self-controlled
and have a distinct behaviour.
