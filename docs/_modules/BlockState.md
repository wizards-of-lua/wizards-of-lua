---
title: What We Can Know About a Block
name: BlockState
properties:
  - name: name
    type: string
    access: r
    description: "This is the name of the block, e.g. 'grass', 'sand', or 'dirt'."
  - name: properties
    type: table
    access: r
    description: "The 'properties' are a table of block-specifc key-value pairs.
    For example, a grass block has a property called 'snowy' which can be true or false, and
    a furnace has a property called 'facing' which can be one of 'NORTH', 'EAST', 'SOUTH', and 'WEST'.
    "
  - name: material
    type: "[Material](!SITE_URL!/modules/Material/)"
    access: r
    description: "
    The 'material' give you some insights in how this block behaves.
    Please have a look into the [Material Book](!SITE_URL!/modules/Material/) for more information.
    "
functions:
---
{% include module-head.md %}

The BlockState represents the 'block' aspects of a block.

{% include module-body.md %}
