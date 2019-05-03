---
name: BlockHit
title: BlockHit
subtitle: What is in Sight
type: class
extends: Object
layout: module
properties:
  - name: hitVec
    type: '[Vec3](/modules/Vec3)'
    access: r
    description: |
        This is the exact position where the scan hit the block.
  - name: pos
    type: '[Vec3](/modules/Vec3)'
    access: r
    description: |
        This is the position of the block that was hit by the scan.
  - name: sideHit
    type: 'string'
    access: r
    description: |
        This is the name of the block's side where the scan hit the block. This can be one of 'down',
        'up', 'south', 'west', 'north', and 'east'.
functions:
---

The <span class="notranslate">BlockHit</span> class contains the results of a call to the
entity's [scanView()](/modules/Entity/#scanView) function.
