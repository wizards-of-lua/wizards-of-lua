---
name: Mob
title: Mob
subtitle: Mobile Creatures
type: class
extends: Entity
layout: module
properties:
  - name: 'ai'
    type: 'boolean'
    access: r/w
    description: |
        The 'ai' property defines whether this mobile creature is currently controlled by its
        artificial intelligence (AI). If set to false this creature becomes dumb and just stands
        around. It even won't react to physical forces. Default is true.
  - name: 'health'
    type: 'number (float)'
    access: r/w
    description: |
        The 'health' is the energy of this entity. When it falls to zero this entity dies.
  - name: 'mainhand'
    type: '[Item](/modules/Item)'
    access: r/w
    description: |
        This is the [item](/modules/Item) this entity is holding in its main hand.
  - name: 'offhand'
    type: '[Item](/modules/Item)'
    access: r/w
    description: |
        This is the [item](/modules/Item) this entity is holding in his off hand.
functions:
---

The <span class="notranslate">Mob</span> class represents mobile creatures that are
self-controlled and have a distinct behaviour.
