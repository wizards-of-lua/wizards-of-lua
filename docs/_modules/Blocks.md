---
title: Uncovering the Internal Values of Blocks
name: Blocks
properties:
functions:
  - name: getNbt
    parameters: Vec3
    results: NBT-Data
    description: "The 'getNbt' function returns the NBT-Data for the block
    at the given position.
    "
    examples:
      - url: Blocks/getNbt.md
  - name: putNbt
    parameters: Vec3, table
    results: nil
    description: "The 'putNbt' function merges the given table contents into
    the NBT-Data of the block at the given position.
    "
    examples:
      - url: Blocks/putNbt.md
---
{% include module-head.md %}

The Blocks module provides access to the internal structure and values of
specific blocks like furnance and chest.

{% include module-body.md %}
