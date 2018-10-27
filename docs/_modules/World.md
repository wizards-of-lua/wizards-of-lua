---
name: World
title: World
subtitle: Where All Exists
type: class
extends: Object
layout: module
properties:
  - name: daytime
    type: 'number (long)'
    access: r
    description: |
        The 'daytime' is the number of game ticks that have passed since the last sunrise. In
        Mincraft the day runs from sunrise to sunrise and is divided into 24000 game ticks. For
        example, 0 means sunrise, 6000 noon, 12000 sunset, 18000 midnight, and 23999 is the end of
        the night.
  - name: difficulty
    type: 'string'
    access: r/w
    description: |
        The difficulty defines how difficult it is for the players to live in this world. This is one
        of PEACEFUL, EASY, NORMAL, HARD.
  - name: dimension
    type: 'number (int)'
    access: r
    description: |
        The 'dimension' is a magic number that defines which kind of world this is. 0 means the
        Overworld. -1 is the Nether, and 1 is the End.
  - name: name
    type: 'string'
    access: r
    description: |
        This is the name of this world.
  - name: spawnPoint
    type: '[Vec3](/modules/Vec3)'
    access: r/w
    description: |
        The spawn point is a certain point in this world where [Players](/module/Player) will spawn
        when they enter the world the first time, or when their personal spawn point is somehow not
        accessible anymore.
  - name: time
    type: 'number (long)'
    access: r/w
    description: |
        The 'time' is the number of game ticks that have passed since the world has been created. But
        in contrast to [Time.gametime](/modules/Time#gametime) this value can be modified by
        operators using the <tt>/time</tt> command.
functions:
  - name: canSeeSky
    parameters: pos
    results: 'boolean'
    description: |
        The 'canSeeSky' function returns true if the sky is visible from the given position when
        looking straight up.
  - name: getNearestVillage
    parameters: position, radius
    results: '[Vec3](/modules/Vec3)'
    description: |
        The 'getNearestVillage' function returns the village center of the nearest
        [village](https://minecraft.gamepedia.com/Tutorials/Village_mechanics) relative to the given
        position, or nil, if no village is found within the given radius.
  - name: isGeneratedAt
    parameters: pos
    results: 'boolean'
    description: |
        The 'isGeneratedAt' function returns true if the world chunk that contains the given world
        coordinates has already been generated. Please note that this doesn't imply that this chunk
        also is currently loaded.
  - name: isLoadedAt
    parameters: pos
    results: 'boolean'
    description: |
        The 'isLoadedAt' function returns true if the world chunk that contains the given world
        coordinates is currently loaded into the server.
---

The World is the space around every creature and item in it.
