---
title: Controlling an Organic or Inorganic Entity
name: Entity
properties:
  - name: id
    type: string
    access: r
    description: "The 'id' is a unique string of 36 characters that identifies an entity
    inside the world. That means that entities with the same ID are in fact the same object.
    "
  - name: name
    type: string
    access: r/w
    description: "The 'name' of the entity is unlike the ID not unique in the world.
    For most entities it is just something like 'Pig' or 'Zombie'. For player entities
    it is the nickkname of the character, like 'mickkay' or 'bytemage'.
    "
  - name: dimension
    type: number
    access: r/w
    description: "The 'dimension' is a magic number that tells us something about
    the world where this entity currently is living in. 0 means the Overworld.
    -1 is the Nether, and 1 is the End.
    "
  - name: pos
    type: Vec3
    access: r/w
    description: "The 'pos' is short for 'position'. It is a 3-dimensional vector
    containing the location of the entity inside the world it is living in.
    "
  - name: blockPos
    type: Vec3
    access: r
    description: "The 'blockPos' is the position of the block
    where the entity is currently located. A block is a logical cube with
    one meter in length. That means that the vector components are always
    integer numbers.  
    "
  - name: eyeHeight
    type: number
    access: r
    description: "The 'eyeHeight' is the y-offset from the entity's feet position
    to the point where the entity's eyes are located, of course only if it has
    any at all.
    For entities that have no eyes, this normally is the upper end of their
    body.
    "
  - name: orientation
    type: string
    access: r
    description: "The 'orientation' denotes the direction the entity is oriented at.
    This is one of 'NORTH', 'EAST', 'SOUTH', or 'WEST'. It defines for example
    in which direction the entity will move when it walks forward.
    "
  - name: rotationYaw
    type: number
    access: r/w
    description: "The 'rotationYaw' is the angle (in degrees) between South and
    the direction the entity is oriented at. It can have any value between -180
    to 180, where 0 means 'SOUTH'. For example, when the entity is
    facing South-West, its value will be 45.
    "
  - name: rotationPitch
    type: number
    access: r/w
    description: "The 'rotationPitch' is the vertical angle (in degrees) of the
    entity's head ranging between -90 (straigth up), 0 (forward) and
    90 (straight down).
    "
  - name: lookVec
    type: Vec3
    access: r
    description: "The 'lookVec' is the vector that points into the same direction
    the entity is currently looking at, of course only if it has any eyes to
    look with in the first place. If not, the value is nil.
    "
  - name: team
    type: string
    access: r
    description: "The 'team' is the name of the team this entity belongs to, or
    nil if it is not a member of any team.
    "
  - name: tags
    type: table
    access: r
    description: "The 'tags' is a table with all the tags this entity is marked
    with. See function `addTag()` to find out how to mark an entity with a tag.
    "
  - name: facing
    type: string
    access: r
    description: "The 'facing' denotes the direction the entity is oriented at.
    It has mostly the same value as 'orientation'. For some few entity types
    it can vary, for example for the Boat and the Minecart.
    "
  - name: motion
    type: Vec3
    access: r/w
    description: "The 'motion' is a vector that points into the direction this
    entity is currently moving to, controlled by an external force.
    That means, if it is walking there by itself, the motion vector is (0,0,0).
    "
functions:
  - name: addTag
    parameters: tag
    results: nil
    description: "The 'addTag' function marks the current entity with the
    given tag."
  - name: removeTag
    parameters: tag
    results: nil
    description: "The 'removeTag' function removes the given tag from the
    current entity."
  - name: setTags
    parameters: tags
    results: nil
    description: "The 'setTags' function marks the current entity with the
    given tags. All other tags this entity had before, will be removed."
  - name: hasTag
    parameters: tag
    results: bool
    description: "Searches the current entity's tags for the given tag and
    returns true if it finds it, false otherwise."
  - name: getNbt
    parameters:
    results: table
    description: "The 'getNbt' function returns a table with the NBT-Data
    properties of this entity."
  - name: putNbt
    parameters: table
    results: nil
    description: "The 'putNbt' function merges the given table contents into
    the NBT-Data properties of this entity."
---
{% include module-head.md %}

TODO

{% include module-body.md %}
