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
  - name: mainhand
    type: "[Item](!SITE_URL!/modules/Item/)"
    access: r/w
    description: "This is the [item](!SITE_URL!/modules/Item/) this creature is holding in its main hand.    
    "
  - name: offhand
    type: "[Item](!SITE_URL!/modules/Item/)"
    access: r/w
    description: "This is the [item](!SITE_URL!/modules/Item/) this creature is holding in its off hand.    
    "
functions:
---

The Mob class represents mobile creatures that are self-controlled
and have a distinct behaviour.
