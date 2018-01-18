---
name: Entity
subtitle: The Base Class of all Organic or Inorganic Entities
type: class
layout: module
properties:
  - name: uuid
    type: string
    access: r
    description: "The 'uuid' is a string of 36 characters forming an immutable universally
    unique identifier that identifies this entity inside the world.
    This means if entities have the same ID they are actually the same object.
    "
  - name: name
    type: string
    access: r/w
    description: "The 'name' of this entity is unlike the UUID not unique in the world.
    For most entities it is just something like 'Pig' or 'Zombie'. For player entities
    it is the nickkname of the character, like 'mickkay' or 'bytemage'.
    "
  - name: dimension
    type: number
    access: r
    description: "The 'dimension' is a magic number that tells us something about
    the world where this entity currently is living in. 0 means the Overworld.
    -1 is the Nether, and 1 is the End.
    "
  - name: pos
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r/w
    description: "The 'pos' is short for 'position'. It is a 3-dimensional vector
    containing the location of the entity inside the world it is living in.
    "
  - name: nbt
    type: table
    access: r
    description: "The 'nbt' value (short for Named Binary Tag) is a table of entity-specifc key-value pairs
    also called [data tags](https://minecraft.gamepedia.com/Commands#Data_tags).
    The nbt property is readonly but gives you a modifiable copy of the internal value. You can change the contents, but to activate them you have to assign the modified table to the entity by using the [putNbt()](/modules/Entity/#putNbt) function.
    "
    examples:
      - url: Entity/nbt.md
  - name: orientation
    type: string
    access: r
    description: "The 'orientation' is the compass direction this entity is facing. This is one of 'north', 'east', 'south', and 'west'.
    "
  - name: lookVec
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r/w
    description: "The 'lookVec' is a 3-dimensional vector that points into the direction this entity is looking at, or nil, if it
    is not looking anywhere, for example, if it has no eyes.
    "
    examples:
      - url: Entity/lookVec.md
  - name: rotationYaw
    type: number
    access: r/w
    description: "The 'rotationYaw' is the rotation of this entity around its Y axis in degrees.
    For example, a value of 0 means the entity is facing south. 90 corresponds to west, and 45 to south-west.
    "
  - name: rotationPitch
    type: number
    access: r/w
    description: "The 'rotationPitch' is the rotation of this entity's head around its X axis in degrees. A value of -90 means the entity is looking straight up. A value of 90 means it is looking straight down.
    "
  - name: eyeHeight
    type: number
    access: r
    description: "The 'eyeHeight' is the distance from this entity's feet to its eyes in Y direction.
    "
  - name: motion
    type: "[Vec3](!SITE_URL!/modules/Vec3/)"
    access: r/w
    description: "The 'motion' is a 3-dimensional vector that represents the velocity of this entity when it is moved by some
    external force, e.g. when it is falling or when it is pushed by an explosion.
    "
    examples:
      - url: Entity/motion.md
  - name: tags
    type: table
    access: r/w
    description: "The 'tags' value is a list of strings that have been assigned to this entity.
    "
    examples:
      - url: Entity/tags.md
functions:
  - name: move
    parameters: direction, distance
    results: nil
    description: "The 'move' function teleports this entity instantly to the position
    relative to its current position specified by the given direction and distance.
    If no distance is specified, 1 meter is taken as default distance.
    Valid direction values are absolute directions ('up', 'down', 'north', 'east',
    'south', and 'west'), as well as relative directions ('forward', 'back',
    'left', and 'right'). Relative directions are interpreted relative to the entity's
    [orientation](!SITE_URL!/modules/Entity/#orientation).
    "
    examples:
      - url: Entity/move.md
  - name: putNbt
    parameters: table
    results: nil
    description: "The 'putNbt' function inserts the given table entries into the
    [nbt](!SITE_URL!/modules/Entity/#nbt) property of this entity.
    Please note that this function is not supported for
    [Player](!SITE_URL!/modules/Player/) objects.
    "
    examples:
      - url: Entity/putNbt.md
  - name: addTag
    parameters: string
    results: boolean
    description: "The 'addTag' function adds the given tag to the set of [tags](!SITE_URL!/modules/Entity/#tags) of this entity.
    This function returns true if the tag was added successfully.
    "
  - name: removeTag
    parameters: string
    results: boolean
    description: "The 'removeTag' function removes the given tag from the set of [tags](!SITE_URL!/modules/Entity/#tags) of this entity.
    This function returns true if the tag has been removed successfully, and false if there was no such tag.
    "
  - name: scanView
    parameters: distance
    results: "[BlockHit](!SITE_URL!/modules/BlockHit/)"
    description: "The 'scanView' function scans the view of this entity for the next
    (non-liquid) block. On success it returns a [BlockHit](!SITE_URL!/modules/BlockHit/), otherwise nil.
    It scans the view with a line-of-sight-range of up to the given distance (meter).
    "
    examples:
      - url: Entity/scanView.md
  - name: dropItem
    parameters: "[Item](!SITE_URL!/modules/Item/), verticalOffset"
    results: "[DroppedItem](!SITE_URL!/modules/DroppedItem/)"
    description: "The 'dropItem' function drops the given item at this entity's position modified by the optionally given vertical offset.
    "
    examples:
      - url: Entity/dropItem.md
---

The Entity class is the base class of all entities that populate the world.
