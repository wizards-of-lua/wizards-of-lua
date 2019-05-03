---
name: Entity
title: Entity
subtitle: The Base Class of all Organic or Inorganic Entities
type: class
extends: Object
layout: module
properties:
  - name: alive
    type: 'boolean'
    access: r
    description: |
        This is true, if this entity is alive, false otherwise.
  - name: dimension
    type: 'number (int)'
    access: r
    description: |
        The 'dimension' is a magic number that tells us something about the world where this entity
        currently is living in. 0 means the Overworld. -1 is the Nether, and 1 is the End.
  - name: entityType
    type: 'string'
    access: r
    description: |
        The 'entity type' of this entity is something like 'pig' or 'creeper'. For a player this is
        "player". This is nil if the entity type isn't known.
  - name: eyeHeight
    type: 'number (float)'
    access: r
    description: |
        The 'eyeHeight' is the distance from this entity's feet to its eyes in Y direction.
  - name: facing
    type: 'string'
    access: r
    description: |
        The 'facing' is the compass direction this entity is facing. This is one of 'north', 'east',
        'south', and 'west'.
  - name: invisible
    type: 'boolean'
    access: r
    description: |
        The 'invisible' property is true if this entity can not be seen by others.
  - name: lookVec
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'lookVec' is a 3-dimensional vector that points into the direction this entity is looking
        at, or nil, if it is not looking anywhere, for example, if it has no eyes.
       
        #### Example
       
        Letting the wizard spit 10 meters into the direction he is looking at.
       
        ```lua
        local dir=spell.owner.lookVec
        local s=spell.owner.pos+Vec3(0,spell.owner.eyeHeight,0)
        for i=1,10,0.1 do
          spell.pos=s+dir*i
          spell:execute("particle droplet ~ ~ ~ 0 0 0 0")
        end
        ```
       
        #### Example
       
        Moving the spell into the owners eye and pointing it into the owner's look direction.
       
        ```lua
        spell.pos = spell.owner.pos+Vec3(0,spell.owner.eyeHeight,0);
        spell.lookVec = spell.owner.lookVec
        ```
  - name: motion
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'motion' is a 3-dimensional vector that represents the velocity of this entity when it is
        moved by some external force, e.g. when it is falling or when it is pushed by an explosion.
       
        #### Example
       
        Pushing the wizard up into the sky.
       
        ```lua
        spell.owner.motion=Vec3(0,5,0)
        ```
  - name: name
    type: 'string'
    access: r/w
    description: |
        The 'name' of this entity is unlike the UUID not unique in the world. For most entities it is
        just something like 'Pig' or 'Zombie'. For player entities it is the nickkname of the
        character, like 'mickkay' or 'bytemage'.
  - name: nbt
    type: '[table](/modules/table)'
    access: r
    description: |
        The 'nbt' value (short for Named Binary Tag) is a table of entity-specifc key-value pairs
        also called [data tags](https://minecraft.gamepedia.com/Commands#Data_tags).
       
        The nbt property is readonly but gives you a modifiable copy of the internal value.
       
        You can change the contents, but to activate them you have to assign the modified table to
        the entity by using the [putNbt()](/modules/Entity/#putNbt) function.
       
        #### Example
       
        Putting on a helmet on all zombies.
       
        ```lua
        for _,zombie in pairs(Entities.find("@e[type=zombie]")) do
          local n=zombie.nbt
          n.ArmorItems[4]={Count=1,id="iron_helmet"}
          zombie:putNbt(n)
        end
        ```
  - name: pos
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'pos' is short for 'position'. It is a 3-dimensional vector containing the location of
        the entity inside the world it is living in.
  - name: rotationPitch
    type: 'number (float)'
    access: r/w
    description: |
        The 'rotationPitch' is the rotation of this entity's head around its X axis in degrees. A
        value of -90 means the entity is looking straight up. A value of 90 means it is looking
        straight down.
  - name: rotationYaw
    type: 'number (float)'
    access: r/w
    description: |
        The 'rotationYaw' is the rotation of this entity around its Y axis in degrees. For example, a
        value of 0 means the entity is facing south. 90 corresponds to west, and 45 to south-west.
  - name: sneaking
    type: 'boolean'
    access: r
    description: |
        This is true, if this entity is currently sneaking, false otherwise.
  - name: sprinting
    type: 'boolean'
    access: r
    description: |
        The 'sprinting' property is true whenever this entity is running fast.
  - name: tags
    type: 'table'
    access: r/w
    description: |
        The 'tags' value is a list of strings that have been assigned to this entity.
       
        #### Example
       
        Tagging the wizard as great and fearsome.
       
        ```lua
        spell.owner.tags = {"great", "fearsome"}
        ```
       
        #### Example
       
        Printing the wizard's tags.
       
        ```lua
        print( str( spell.owner.tags))
        ```
  - name: uuid
    type: 'string'
    access: r
    description: |
        The 'uuid' is a string of 36 characters forming an immutable universally unique identifier
        that identifies this entity inside the world. This means if entities have the same ID they
        are actually the same object.
  - name: world
    type: '[World](/modules/World)'
    access: r
    description: |
        The world the the space this entity is living in.
functions:
  - name: addTag
    parameters: tag
    results: 'boolean'
    description: |
        The 'addTag' function adds the given tag to the set of [tags](/modules/Entity/#tags) of this
        entity. This function returns true if the tag was added successfully.
  - name: dropItem
    parameters: item, offsetY
    results: '[DroppedItem](/modules/DroppedItem)'
    description: |
        The 'dropItem' function drops the given item at this entity's position modified by the
        optionally given vertical offset.
       
        #### Example
       
        Dropping the block at the spell's position as an item.
       
        ```lua
        if spell.block.name ~= "air" then
          local item = spell.block:asItem()
          spell:dropItem( item)
          spell.block = Blocks.get("air")
        end
        ```
  - name: kill
    parameters: 
    results: 'nil'
    description: |
        The 'kill' function kills this entity during the next game tick.
       
        #### Example
       
        Killing all pigs that are swimming in liquid material.
       
        ```lua
        local pigs = Entities.find("@e[type=pig]")
        for _,pig in pairs(pigs) do
          spell.pos = pig.pos
          if spell.block.material.liquid then
            pig:kill()
          end
        end
        ```
  - name: move
    parameters: directionName, distance
    results: 'nil'
    description: |
        The 'move' function teleports this entity instantly to the position relative to its current
        position specified by the given direction and distance.
       
        If no distance is specified, 1 meter is taken as default distance. Valid direction values are
        absolute directions ('up', 'down', 'north', 'east', 'south', and 'west'), as well as relative
        directions ('forward', 'back', 'left', and 'right').
       
        Relative directions are interpreted relative to the direction the entity is
        [facing](/modules/Entity/#facing).
       
        #### Example
       
        Moving the spell's owner for half a meter to the left.
       
        ```lua
        spell.owner:move( "left", 0.5)
        ```
  - name: putNbt
    parameters: nbt
    results: 'nil'
    description: |
        The 'putNbt' function inserts the given table entries into the [nbt](/modules/Entity/#nbt)
        property of this entity.
       
        Please note that this function is not supported for [Player](/modules/Player/) objects.
       
        #### Example
       
        Cutting the health of all bats to half.
       
        ```lua
        local e = Entities.find("@e[type=bat]")
        for _,bat in pairs(e) do
          local h = math.floor(bat.nbt.Health/2)
          bat:putNbt({Health=h})
          print(bat.nbt.Health)
        end
        ```
       
        #### Example
       
        Finding all pigs and putting a saddle on each of them.
       
        ```lua
        for _,pig in pairs(Entities.find("@e[type=pig]")) do
          pig:putNbt({Saddle=1})
        end
        ```
  - name: removeTag
    parameters: tag
    results: 'boolean'
    description: |
        The 'removeTag' function removes the given tag from the set of [tags](/modules/Entity/#tags)
        of this entity. This function returns true if the tag has been removed successfully, and
        false if there was no such tag.
  - name: scanView
    parameters: distance
    results: '[BlockHit](/modules/BlockHit)'
    description: |
        The 'scanView' function scans the view of this entity for the next (non-liquid) block.
       
        On success it returns a [BlockHit](/modules/BlockHit/), otherwise nil. It scans the view with
        a line-of-sight-range of up to the given distance (meter).
       
        #### Example
       
        Prints the name of the block the spell's owner is looking at (up to a maximum distance of 10
        meters).
       
        ```lua
        local maxDistance = 10
        local hit = spell.owner:scanView( maxDistance)
        if hit then
          spell.pos = hit.pos
          print(spell.owner.name.." is looking at "..spell.block.name)
        end
        ```
---

The Entity class is the base class of all entities that populate the world.
