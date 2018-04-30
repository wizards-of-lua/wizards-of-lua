---
name: Spell
subtitle: Aspects of an Active Spell
type: class
extends: VirtualEntity
layout: module
properties:
  - name: block
    type: "[Block](/modules/Block/)"
    access: r/w
    description: "The 'block' denotes the *block's state* at the spell's position. Use
    it to find out about what material the block is constructed of, or in which
    direction it is facing.
    "
    examples:
      - url: Spell/block.md
  - name: owner
    type: "[Entity](/modules/Entity/)"
    access: r
    description: The entity that has casted this spell. Normally this is a Player, or nil if the spell has been casted by a command block.
    examples:
      - url: Spell/owner.md
  - name: visible
    type: boolean
    access: r/w
    description: The 'visible' property defines if this spell is visible for players.
    examples:
      - url: Spell/visible.md
  - name: data
    type: table
    access: r/w
    description: |
      The 'data' property is a spell-specific table that can hold custom key-value pairs.
      These entries are modifiable not only by the owning spell itself but also by all other spells.
      Therefore the data property is very well suited for exchanging information between spells.

      #### Example
      Storing a copy of the block at the spell's position into the 'block' entry of the spell's data property.
      ```lua
      spell.data.block = spell.block:copy()
      ```

      #### Example
      Creating a spell called 'rain-spell' that is dropping a lot of copies of a specific item that is defined in the
      'item' entry of the spell's data property.
      ```lua
      spell.name = 'rain-spell'
      spell.visible = true
      while true do
        local item = spell.data.item
        if item then
          spell:dropItem(item)
        end
        local dx = math.random(-1,1)
        local dz = math.random(-1,1)
        spell.pos = spell.pos + Vec3(dx,0,dz)
        sleep(math.random(10,60))
      end
      ```

      Finding the (first) spell called 'rain-spell' and setting the item that is 'raining' down.
      ```lua
      local otherSpellName = 'rain-spell'
      local otherSpell = Spells.find({name=otherSpellName})[1]
      otherSpell.data.item = Items.get('diamond_axe')
      ```
functions:
  - name: execute
    parameters: command, ...
    results: nil
    description: "This function executes the given Minecraft command as if it was
    entered into the chat window.


    The owner of the current spell will be treated as the new command's sender.
    The new command is executed at the current spell's position.

    This function supports additional arguments which are 'formatted'
    into special placeholders in the command string. See [`string.format()`](http://lua-users.org/wiki/StringLibraryTutorial)
    for more information.
    "
    examples:
      - url: Spell/execute.md
---

The <span class="notranslate">Spell</span> is one of the main magic classes used in most known spells. It is used to
control the properties and the behaviour of the executed spell itself.
