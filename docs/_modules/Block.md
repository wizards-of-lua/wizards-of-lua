---
name: Block
title: Block
subtitle: All There is to Know About a Block
type: class
extends: Object
layout: module
properties:
  - name: data
    type: '[table](/modules/table)'
    access: r
    description: |
        The 'data' value is a table of block-specifc key-value pairs that provide human readable
        information about the [block's data](https://minecraft.gamepedia.com/Data_values#Data). For
        example, a grass block has a property called 'snowy' which can be true or false, and a
        furnace has a property called 'facing' which can be one of 'north', 'east', 'south', and
        'west'.
  - name: material
    type: '[Material](/modules/Material)'
    access: r
    description: |
        The 'material' give you some insights in how this block behaves. Please have a look into the
        [Material Book](/modules/Material/) for more information.
  - name: name
    type: 'string'
    access: r
    description: |
        This is the basic name of the block, e.g. 'grass', 'stone', or 'air'.
  - name: nbt
    type: '[table](/modules/table)'
    access: r
    description: |
        The 'nbt' value (short for Named Binary Tag) is a table of block-specifc key-value pairs
        about the [block's entity](https://minecraft.gamepedia.com/Block_entity_format). Only a small
        amount of blocks do have a block entity. For example, the sign's entity contains information
        about its text, and the chest's entity contains information about its content.
functions:
  - name: asItem
    parameters: amount
    results: '[Item](/modules/Item)'
    description: |
        The 'asItem' function returns this block as an [item](/modules/Item/) of the given amount.
       
        #### Example
       
        Creating an item from the block at the spell's current position and putting it into the
        wizard's offhand.
       
        ```lua
        item=spell.block:asItem(); spell.owner.offhand=item
        ```
       
        Creating a full stack of of the block at the spell's current position and putting it into the
        wizard's offhand.
       
        ```lua
        item=spell.block:asItem(64); spell.owner.offhand=item
        ```
  - name: copy
    parameters: 
    results: '[Block](/modules/Block)'
    description: |
        the 'copy' function returns a copy of this block.
  - name: withData
    parameters: data
    results: '[Block](/modules/Block)'
    description: |
        The 'withData' function returns a modified copy of the given block with the given table
        values as the [block's data](https://minecraft.gamepedia.com/Data_values#Data).
       
        #### Example
       
        Creating a smooth diorite block and placing it at the spell's position.
       
        ```lua
        spell.block = Blocks.get( "stone"):withData(
          { variant = "smooth_diorite"}
        )
        ```
       
        #### Example
       
        Creating a bundle of full grown wheat on top of the block at the spell's position.
       
        ```lua
        spell:move( "up")
        spell.block = Blocks.get( "wheat"):withData(
          { age = 7}
        )
        ```
  - name: withNbt
    parameters: nbt
    results: '[Block](/modules/Block)'
    description: |
        The 'withNbt' function returns a modified copy of this block with the given table values for
        the [block's entity](https://minecraft.gamepedia.com/Block_entity_format).
       
        #### Example
       
        Creating a standing sign with the name of the current spell's owner written onto it and
        placing it at the spell's position.
       
        ```lua
        spell.block = Blocks.get( "standing_sign"):withNbt( {
          Text1 = '{"text":"'..spell.owner.name..'"}'
        })
        ```
       
        #### Example
       
        Creating a wall sign showing the current time.
       
        ```lua
        spell:move("back")
        spell.rotationYaw=spell.rotationYaw+180
        spell.block=Blocks.get("wall_sign"):withData({facing=spell.facing})
        while true do
          local time=Time.getDate("HH:mm:ss")
          spell.block=spell.block:withNbt({Text1= '{"text":"'..time..'"}'})
          sleep(20)
        end
        ```
       
        #### Example
       
        Putting a stack of 64 wheat bundles into slot no. 5 of the chest (or the shulker box) at the
        spell's position.
       
        ```lua
        spell.block = spell.block:withNbt( {
          Items = {
            { Count = 64, Damage = 0, Slot = 5,
              id = "minecraft:wheat"
            }
          }
        })
        ```
---

The <span class="notranslate">Block</span> class is a basic unit of structure in Minecraft.

An instance of this class represents either one of the following types:

1. a live block reference that represents a block at a specific world position. It can be
accessed by [spell.block](/modules/Spell/#block). 'Live' means, that whenever the block at that
position changes, the internal state of this object will change to.

2. an immutable block value that can exists independent of the world. It can be created by
calling [Blocks.get()](/modules/Blocks/#get) or [Block:copy()](/modules/Block/#copy).

Both types are 'unmodifiable', meaning that you can't change their internal states directly.
Instead, if you want to change a block in the world, you will need to assign a new value to the
[spell.block](/modules/Spell/#block) field. This will copy the state of the right-hand value into
the block at the given spell's position.
