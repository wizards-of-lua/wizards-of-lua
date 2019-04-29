---
name: Spell
title: Spell
subtitle: Aspects of an Active Spell
type: class
extends: VirtualEntity
layout: module
properties:
  - name: block
    type: '[Block](/modules/Block)'
    access: r/w
    description: |
        The 'block' denotes the *block's state* at the spell's position. Use it to find out about
        what material the block is constructed of, or in which direction it is facing.
       
        #### Example
       
        Printing the name of the block at the spell's position.
       
        ```lua
        print(spell.block.name)
        ```
       
        #### Example
       
        Inspecting the block the spell's position.
       
        ```lua
        print(str(spell.block))
        ```
       
        #### Example
       
        Changing the block at the spell's position into dirt.
       
        ```lua
        spell.block = Blocks.get("dirt")
        ```
       
        #### Example
       
        Copying the block at the spell's position 10 times upwards.
       
        ```lua
        local copy = spell.block
        for i=1,10 do
          spell:move("up")
          spell.block = copy
        end
        ```
  - name: data
    type: 'table'
    access: r
    description: |
        The 'data' property is a spell-specific table that can hold custom key-value pairs. These
        entries are modifiable not only by the owning spell itself but also by all other spells.
        Therefore the data property is very well suited for exchanging information between spells.
       
        #### Example
       
        Storing a copy of the block at the spell's position into the 'block' entry of the spell's
        data property.
       
        ```lua
        spell.data.block = spell.block:copy()
        ```
       
        #### Example
       
        Creating a spell called 'rain-spell' that is dropping a lot of copies of a specific item that
        is defined in the 'item' entry of the spell's data property.
       
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
  - name: owner
    type: '[Entity](/modules/Entity)'
    access: r
    description: |
        The entity that has casted this spell. Normally this is a Player, or nil if the spell has
        been casted by a command block.
       
        #### Example
       
        Printing the name of this spell's onwer.
       
        ```lua
        print( spell.owner.name )
        ```
  - name: sid
    type: 'number (long)'
    access: r
    description: |
        The 'sid' is the spell's numerical id.
       
        #### Example
       
        Breaking each spell in the range of 10 meters.
       
        ```lua
        found = Spells.find({maxradius=10})
        for _,s in pairs(found) do
          spell:execute("wol spell break bySid %s", s.sid)
        end
        ```
  - name: visible
    type: 'boolean'
    access: r/w
    description: |
        The 'visible' property defines if this spell is visible for players.
       
        #### Example
       
        Making the spell visible.
       
        ```lua
        spell.visible = true
        ```
       
        #### Example
       
        Making the spell visible and moving it around in a circle.
       
        ```lua
        spell.visible = true
        start = spell.pos
        for a=0,math.pi*2,0.1 do
          z = math.sin(a)
          x = math.cos(a)
          r = 3
          spell.pos = start + Vec3(x,0,z) * r
          sleep(1)
        end
        ```
functions:
  - name: execute
    parameters: command, ...
    results: 'number'
    description: |
        This function executes the given Minecraft command.
       
        When specifying the command the leading slash '/' character is optional.
       
        This function supports additional arguments which are 'formatted' into placeholders that must
        be present in the command string. See
        [`string.format()`](http://lua-users.org/wiki/StringLibraryTutorial) for more information.
       
        The current spell will be treated as the new command's sender. The new command will be
        executed at the current spell's position.
       
        If the new command is a "lua" command, then the new spell inherits the current spell's owner.
       
        #### Example
       
        Setting the game time to 'day'.
       
        ```lua
        spell:execute("time set day")
        ```
       
        #### Example
       
        Letting the current spell say "hello".
       
        ```lua
        spell:execute([[/say hello]])
        ```
       
        #### Example
       
        Letting the player "mickkay" say "hello":
       
        ```lua
        spell:execute([[/execute mickkay ~ ~ ~ say hello]])
        ```
       
        #### Example
       
        Spawning a zombie at the spell's current location.
       
        ```lua
        spell:move("up")
        spell:execute("summon zombie ~ ~ ~")
        ```
       
        #### Example
       
        Spawning some smoke particles at the spell's current location.
       
        ```lua
        local particle = "smoke"
        spell:execute("particle %s ~ ~ ~ 0 0 0 0 0", particle)
        ```
       
        #### Example
       
        Building a wall by casting some parallel spells each building a pillar.
       
        ```lua
        for x=1,20 do
          spell:execute([[lua
            for i=1,5 do
              spell.block = Blocks.get("stone")
              sleep(1)
              spell:move("up")
            end
          ]])
          spell:move("north")
        end
        ```
       
        #### Example
       
        Drawing a circle of black smoke with a radius of 1.4 meters around the spell's position.
       
        ```lua
        start = spell.pos
        for a=0,math.pi*2,0.1 do
          z = math.sin(a)
          x = math.cos(a)
          y = 0.6
          r = 1.4
          spell.pos = start + Vec3(x,y,z) * r
          spell:execute("particle largesmoke ~ ~ ~ 0 0 0 0 1")
        end
        ```
---

The <span class="notranslate">Spell</span> is one of the main magic classes used in most known
spells. It is used to control the properties and the behaviour of the executed spell itself.
