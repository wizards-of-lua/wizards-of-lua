---
name: Block
subtitle: All There is to Know About a Block
type: class
layout: module
properties:
  - name: name
    type: string
    access: r
    description: "This is the basic name of the block, e.g. 'grass', 'stone', or 'air'."
  - name: data
    type: table
    access: r
    description: "The 'data' value is a table of block-specifc key-value pairs that
    provide human readable information about the [block's data](https://minecraft.gamepedia.com/Data_values#Data).
    For example, a grass block has a property called 'snowy' which can be true or false, and
    a furnace has a property called 'facing' which can be one of 'north', 'east', 'south', and 'west'.
    "
  - name: nbt
    type: table
    access: r
    description: "The 'nbt' value (short for Named Binary Tag) is a table of block-specifc key-value pairs
    about the [block's entity](https://minecraft.gamepedia.com/Block_entity_format).
    Only a small amount of blocks do have a block entity.
    For example, the sign's entity contains information about its text, and the chest's entity contains
    information about its content.
    "
  - name: material
    type: "[Material](/modules/Material/)"
    access: r
    description: "
    The 'material' give you some insights in how this block behaves.
    Please have a look into the [Material Book](/modules/Material/) for more information.
    "
functions:
  - name: withData
    parameters: table
    results: "[Block](/modules/Block/)"
    description: "The 'withData' function returns a modified copy of the given block with the given table values
    as the [block's data](https://minecraft.gamepedia.com/Data_values#Data).
    "
    examples:
      - url: Block/withData.md
  - name: withNbt
    parameters: table
    results: "[Block](/modules/Block/)"
    description: "The 'withNbt' function returns a modified copy of the given block with the given table values
    for the [block's entity](https://minecraft.gamepedia.com/Block_entity_format).
    "
    examples:
      - url: Block/withNbt.md
  - name: asItem
    parameters: amount
    results: "[Item](/modules/Item/)"
    description: "The 'asItem' function returns this block as an [item](/modules/Item/) of the given amount.
    "
    examples:
      - url: Block/asItem.md
---

The <span class="notranslate">Block</span> class is a basic unit of structure in Minecraft.