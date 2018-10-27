---
name: ChunkEvent
title: ChunkEvent
subtitle:
type: event
extends: Event
layout: module
properties:
  - name: chunkX
    type: 'number (int)'
    access: r
    description: |
        This is the x-component of the chunk coordinate.
  - name: chunkZ
    type: 'number (int)'
    access: r
    description: |
        This is the z-component of the chunk coordinate.
  - name: world
    type: '[World](/modules/World)'
    access: r
    description: |
        This is the world where this event did occur.
functions:
---

The <span class="notranslate">ChunkEvent</span> is the common base class of
 [ChunkLoadEvent](/modules/ChunkLoadEvent) and [ChunkUnloadEvent](/modules/ChunkUnloadEvent).

 Please note that instances of this event could occur asynchronously to the game loop. Hence, if
 you use an event interceptor to handle them, make sure that your code is thread safe.
