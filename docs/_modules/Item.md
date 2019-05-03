---
name: Item
title: Item
subtitle: Things You can Carry Around
type: class
extends: Object
layout: module
properties:
  - name: 'count'
    type: 'number (int)'
    access: r/w
    description: |
        When this item is stackable, this is the number of items stacked.
  - name: 'damage'
    type: 'number (int)'
    access: r/w
    description: |
        This is the numerical damage value of this item.
       
        The higher the value, the more damaged this item is.
       
        A value of 0 means the item is not damaged.
  - name: 'displayName'
    type: 'string'
    access: r/w
    description: |
        This is the name of the item.
  - name: 'id'
    type: 'string'
    access: r
    description: |
        The 'id' indentifies the type of this item.
  - name: 'nbt'
    type: '[table](/modules/table)'
    access: r
    description: |
        The 'nbt' value (short for Named Binary Tag) is a table of [item-specifc key-value
        pairs](https://minecraft.gamepedia.com/Player.dat_format#Item_structure).
  - name: 'repairCost'
    type: 'number (int)'
    access: r/w
    description: |
        This is the number of enchantment levels to add to the base level cost when repairing,
        combining, or renaming this item with an anvil.
       
        If this value is negative, it will effectively lower the cost. However, the total cost will
        never fall below zero.
functions:
  - name: 'putNbt'
    parameters: nbt
    results: 'nil'
    description: |
        The 'putNbt' function inserts the given table entries into this item's
        [nbt](/modules/Item/#nbt) property.
       
        #### Example
       
        Setting the lore (description) of the item in the wizard's hand.
       
        ```lua
        local text1 = "Very important magical stuff."
        local text2 = "Use with caution!"
        local loreTbl = { text1, text2}
        local item = spell.owner.mainhand
        item:putNbt({tag={display={Lore=loreTbl}}})
        ```
---

The <span class="notranslate">Item</span> class represents all things a creature can hold in its
hands and which can be stored in an inventory.
