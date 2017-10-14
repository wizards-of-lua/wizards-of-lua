---
name: Material
subtitle: Physical Properties of Blocks
type: class
layout: module
properties:
  - name: blocksLight
    type: boolean
    access: r
    description: This is true if light can not pass this material. If so it will prevent grass from growing on dirt underneath and kill any grass below it.
  - name: blocksMovement
    type: boolean
    access: r
    description: This is true if entites can not pass this material.
  - name: canBurn
    type: boolean
    access: r
    description: This is true if this material can catch fire.
  - name: liquid
    type: boolean
    access: r
    description: This is true if this material is liquid and can flow.
  - name: opaque
    type: boolean
    access: r
    description: This is true if this material blocks the sight of entities.
  - name: solid
    type: boolean
    access: r
    description: This is true if this material is solid.
  - name: replaceable
    type: boolean
    access: r
    description: This is true if this material can be replaced by other blocks, eg. snow, vines, and tall grass.
  - name: requiresNoTool
    type: boolean
    access: r
    description: This is true if this material can be harvested just by hands.
  - name: mobility
    type: string
    access: r
    description: "This defines, if this material can be pushed, e.g. by a piston.
    The value is one of 'NORMAL', 'DESTROY', 'BLOCK', 'IGNORE'.
    "
functions:
---

The Material class describes the physical behaviour of a [Block]({% link _modules/Block.md %}).
