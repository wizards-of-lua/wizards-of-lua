---
name: DroppedItem
title: DroppedItem
subtitle: Things That are Lying Around
type: class
extends: Entity
layout: module
properties:
  - name: item
    type: '[Item](/modules/Item)'
    access: r/w
    description: |
        This is the [item](/modules/Item/) that has been dropped.
functions:
---

The <span class="notranslate">DroppedItem</span> class represents things that are lying somewhere
and can be collected by players.
