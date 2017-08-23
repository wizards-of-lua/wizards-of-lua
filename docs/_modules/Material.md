---
title: Physical Properties of Blocks
name: Material
properties:
  - name: blocksLight
    type: boolean
    access: r
    description: this is true if light can not pass this material.
  - name: blocksMovement
    type: boolean
    access: r
    description: this is true if entites can not pass this material.
  - name: canBurn
    type: boolean
    access: r
    description: this is true if this material can catch fire.
  - name: isLiquid
    type: boolean
    access: r
    description: this is true if this material is liquid and can float.
  - name: isOpaque
    type: boolean
    access: r
    description: this is true if this material blocks the sight of entities.
  - name: isSolid
    type: boolean
    access: r
    description: this is true if this material is solid.
  - name: isToolNotRequired
    type: boolean
    access: r
    description: this is true if this material can be broken just by hands.
  - name: mobility
    type: boolean
    access: r
    description: this is true if this material is mobile.
functions:
---
{% include module-head.md %}

The Material describes the physical behaviour of a [Block]({% link _modules/Block.md %}).

{% include module-body.md %}
