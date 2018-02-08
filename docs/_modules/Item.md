---
name: Item
subtitle: Things You can Carry Around
type: class
layout: module
properties:
  - name: id
    type: string
    access: r
    description: "The ID indentifies the type of this item.
    "
  - name: displayName
    type: string
    access: r/w
    description: "This is the name of the item.
    "
  - name: damage
    type: number
    access: r/w
    description: "This is the numeric damage value of this item.
    The higher the value the more damaged this item is.
    A value of 0 means the item is not damaged.
    "
  - name: repairCost
    type: number
    access: r/w
    description: "This is the number of enchantment levels to add to the base level cost when repairing, combining, or renaming this item with an anvil. If this value is negative, it will effectively lower the cost.
    However, the total cost will never fall below zero.
    "
  - name: count
    type: number
    access: r/w
    description: "When this item is stackable, this is the number of items stacked.    
    "
  - name: nbt
    type: table
    access: r
    description: "The 'nbt' value (short for Named Binary Tag) is a table of [item-specifc key-value pairs](https://minecraft.gamepedia.com/Player.dat_format#Item_structure).
    "
functions:
  - name: putNbt
    parameters: table
    results: nil
    description: "The 'putNbt' function inserts the given table entries into this item's
    [nbt](!SITE_URL!/modules/Item/#nbt) property.
    "
    examples:
      - url: Item/putNbt.md
---

The <span class="notranslate">Item</span> class represents all things a creature can hold in its hands and which can be stored in an inventory.
