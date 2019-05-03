---
name: VirtualEntity
title: VirtualEntity
subtitle: The Base Class of all Virtual Entities
type: class
extends: Object
layout: module
properties:
  - name: 'alive'
    type: 'boolean'
    access: r
    description: |
        This is true, if this entity is alive, false otherwise.
  - name: 'dimension'
    type: 'number (int)'
    access: r
    description: |
        The 'dimension' is a magic number that tells us something about the world where this entity
        currently is living in. 0 means the Overworld. -1 is the Nether, and 1 is the End.
  - name: 'facing'
    type: 'string'
    access: r
    description: |
        The 'facing' is the compass direction this entity is facing. This is one of 'north', 'east',
        'south', and 'west'.
  - name: 'forceChunk'
    type: 'boolean'
    access: r/w
    description: |
        The 'forceChunk' property specifies whether the current world chunk that contains this entity
        should always stay loaded in the server's memory even when there is no player close to it.
        Default is true.
        
        Please note that Minecraft Forge has an upper limit for the number of 'chunk loading tickets'
        that can be requested per Minecraft mod. By default this value is set to 200, which means
        that effectively a maximum number of 200 spells can exist concurrently with 'forceChunk' set
        to true. However, this value can be configured by setting the attribute
        <tt>defaults.maximumTicketCount</tt> in the file <tt>config/forgeChunkLoading.cfg</tt>.
        
        If 'forceChunk' is set to false and there is no player close to the current world chunk, then
        this chunk will become unloaded eventually and stored to the disk. This means that entities
        that are inside this chunk will not take part in the update cycle anymore and neither can be
        found with [Entities.find()](/modules/Entities#find). Please also note that you can't summon
        any entities inside an unloaded chunk.
        
        However, even when the world chunk has been unloaded, this virtual entity itself will always
        stay active. It will always take part in the update cycle and handle events. Additionally, if
        this entity accesses a block of an unloaded chunk, that chunk will get loaded.
  - name: 'lookVec'
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'lookVec' is a 3-dimensional vector that points into the direction this entity is looking
        at, or nil, if it is not looking anywhere, for example, if it has no eyes.
        
        #### Example
        
        Moving the spell into the owners eye and pointing it into the owner's look direction.
        
        ```lua
        spell.pos = spell.owner.pos + Vec3(0, spell.owner.eyeHeight, 0)
        spell.lookVec = spell.owner.lookVec
        ```
  - name: 'motion'
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'motion' is a 3-dimensional vector that represents the velocity of this entity when it is
        moved by some external force, e.g. when it is falling or when it is pushed by an explosion.
  - name: 'name'
    type: 'string'
    access: r/w
    description: |
        The 'name' of this entity is unlike the UUID not unique in the world. For most entities it is
        just something like 'Pig' or 'Zombie'. For player entities it is the nickkname of the
        character, like 'mickkay' or 'bytemage'. And for spells it is typically the spell's id
        prefixed with "Spell-".
  - name: 'pos'
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The 'pos' is short for 'position'. It is a 3-dimensional vector containing the location of
        the entity inside the world it is living in.
  - name: 'rotationPitch'
    type: 'number (float)'
    access: r/w
    description: |
        The 'rotationPitch' is the rotation of this entity's head around its X axis in degrees. A
        value of -90 means the entity is looking straight up. A value of 90 means it is looking
        straight down.
  - name: 'rotationYaw'
    type: 'number (float)'
    access: r/w
    description: |
        The 'rotationYaw' is the rotation of this entity around its Y axis in degrees. For example, a
        value of 0 means the entity is facing south. 90 corresponds to west, and 45 to south-west.
  - name: 'tags'
    type: 'table'
    access: r/w
    description: |
        The 'tags' value is a list of strings that have been assigned to this entity.
  - name: 'uuid'
    type: 'string'
    access: r
    description: |
        The 'uuid' is a string of 36 characters forming an immutable universally unique identifier
        that identifies this entity inside the world. This means if entities have the same ID they
        are actually the same object.
  - name: 'world'
    type: '[World](/modules/World)'
    access: r
    description: |
        The world is the space this entity is living in.
functions:
  - name: 'addTag'
    parameters: tag
    results: 'boolean'
    description: |
        The 'addTag' function adds the given tag to the set of [tags](/modules/Entity/#tags) of this
        entity. This function returns true if the tag was added successfully.
  - name: 'dropItem'
    parameters: item, offsetY
    results: '[DroppedItem](/modules/DroppedItem)'
    description: |
        The 'dropItem' function drops the given item at this entity's position modified by the
        optionally given vertical offset.
  - name: 'kill'
    parameters: 
    results: 'nil'
    description: |
        The 'kill' function kills this entity during the next game tick.
  - name: 'move'
    parameters: directionName, distance
    results: 'nil'
    description: |
        The 'move' function teleports this entity instantly to the position relative to its current
        position specified by the given direction and distance. If no distance is specified, 1 meter
        is taken as default distance. Valid direction values are absolute directions ('up', 'down',
        'north', 'east', 'south', and 'west'), as well as relative directions ('forward', 'back',
        'left', and 'right'). Relative directions are interpreted relative to the direction the
        entity is [facing](/modules/VirtualEntity/#facing).
        
        #### Example
        
        Moving the spell 1 meter upwards.
        
        ```lua
        spell.move( "up")
        ```
        
        #### Example
        
        Moving the spell 10 meters to the north.
        
        ```lua
        spell.move( "north", 10)
        ```
        
        #### Example
        
        Building a huge circle of wool blocks.
        
        ```lua
        wool=Blocks.get( "wool")
        for i=1,360 do
          spell.block=wool
          spell.rotationYaw=spell.rotationYaw+1
          spell:move("forward")
        end
        ```
  - name: 'removeTag'
    parameters: tag
    results: 'boolean'
    description: |
        The 'removeTag' function removes the given tag from the set of [tags](/modules/Entity/#tags)
        of this entity. This function returns true if the tag has been removed successfully, and
        false if there was no such tag.
  - name: 'scanView'
    parameters: distance
    results: '[BlockHit](/modules/BlockHit)'
    description: |
        The 'scanView' function scans the view of this entity for the next (non-liquid) block. On
        success it returns a [BlockHit](/modules/BlockHit/), otherwise nil. It scans the view with a
        line-of-sight-range of up to the given distance (meter).
        
        #### Example
        
        Prints the name of the block the spell's owner is looking at (up to a maximum distance of 10
        meters).
        
        ```lua
        maxDistance = 10
        hit = spell.owner:scanView( maxDistance)
        if hit then
          spell.pos = hit.pos
          print(spell.owner.name.." is looking at "..spell.block.name)
        end
        ```
---

The <span class="notranslate">VirtualEntity</span> class is the base class of all virtual
entities that populate the world. Virtual entities live on the server only and are never synced
to the client.
