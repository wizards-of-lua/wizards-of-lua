---
name: Blocks
title: Blocks
subtitle: The Building Blocks Directory
type: module
layout: module
properties:
functions:
  - name: get
    parameters: name
    results: '[Block](/modules/Block)'
    description: |
        The 'get' function returns the block with the given name.
       
        #### Example
       
        Creating a stone block and placing it at the spell's position.
       
        ```lua
        spell.block = Blocks.get( "stone")
        ```
       
        #### Example
       
        Creating a smooth diorite block and placing it at the spell's position.
       
        ```lua
        spell.block = Blocks.get( "stone"):withData( { variant = "smooth_diorite"})
        ```
       
        #### Example
       
        Creating a standing sign with the name of the current spell's owner written onto it and placing
        it at the spell's position.
       
        ```lua
        spell.block = Blocks.get("standing_sign"):withNbt( {
          Text1='{"text":"'..spell.owner.name..'"}'
        })
        ```
---

The <span class="notranslate">Blocks</span> module provides access to all [block
types](https://minecraft.gamepedia.com/Block) known in Minecraft.
