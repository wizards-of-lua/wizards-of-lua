---
name: Material
subtitle: Physical Properties of Blocks
type: class
layout: module
properties:
  - name: blocksLight
    type: boolean
    access: r
    description: |
      This is true if light can not pass this material. If so it will prevent grass from growing on dirt underneath and kill any grass below it.
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
    description: |
      This defines, if this material can be pushed, e.g. by a piston.
      The value is one of 'NORMAL', 'DESTROY', 'BLOCK', 'IGNORE'.
  - name: name
    type: string
    access: r
    description: |
      This property contains the name of this material, if known, or nil, if not.
      This is something like 'GRASS', 'WOOD', 'IRON', and many others.

      Please note that you must not confuse this with the [block name](/modules/Block/#name).
      For example, 'IRON' is the material not only of
      'iron_bars', 'iron_block', 'iron_door', 'iron_trapdoor', 'light_weighted_pressure_plate', and 'heavy_weighted_pressure_plate',
      but also of 'gold_block', 'lapis_block', 'diamond_block', 'emerald_block', and 'redstone_block'.

functions:
---

The <span class="notranslate">Material</span> class describes the physical behaviour of a [Block]({% link _modules/Block.md %}).
