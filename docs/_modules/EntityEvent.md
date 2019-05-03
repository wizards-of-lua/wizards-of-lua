---
name: EntityEvent
title: EntityEvent
subtitle: 
type: event
extends: Event
layout: module
properties:
  - name: entity
    type: '[Entity](/modules/Entity)'
    access: r
    description: |
        The entity that this event is about.
functions:
---

The <span class="notranslate">EntityEvent</span> is the base class of events about an entity.
