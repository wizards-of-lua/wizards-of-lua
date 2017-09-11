---
name: Entity
subtitle: Controlling an Organic or Inorganic Entity
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
functions:
  - name: move
    parameters: direction, distance
    results: nil
    description: "The 'move' function teleports this entity instantly to the position
    relative to its current position specified by the given direction and distance.
    If no distance is specified, 1 meter is taken as default distance."
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
---

The Entity class is the base class of all entities that populate the world.
