---
name: World
subtitle: Where All Exists
type: class
layout: module
properties:
  - name: dimension
    type: number
    access: r
    description: |
      The 'dimension' is a magic number that defines which kind of world this is.
      0 means the Overworld. -1 is the Nether, and 1 is the End.
  - name: name
    type: string
    access: r
    description: This is the name of this world.
  - name: spawnPoint
    type: "[Vec3](/modules/Vec3/)"
    access: r/w
    description: |
      The spawn point is a certain point in this world where [Players](/module/Player) will spawn
      when they enter the world the first time, or when their personal spawn point is somehow not
      accessible anymore.
  - name: difficulty
    type: string
    access: r/w
    description: |
      The difficulty defines how difficult it is for the players to live in this world.
      This is one of PEACEFUL, EASY, NORMAL, HARD.
functions:
  - name: canSeeSky
    parameters: pos
    results: boolean
    description: |
      The 'canSeeSky' function returns true if the sky is visible from the given position when
      looking straight up.
  - name: getNearestVillage
    parameters: pos, radius
    results: "[Vec3](/modules/Vec3/)"
    description: |
      The 'getNearestVillage' function returns the village center of the nearest
      [village](https://minecraft.gamepedia.com/Tutorials/Village_mechanics) relative to the given
      position, or nil, if no village is found within the given radius.
---

The <span class="notranslate">Spell</span> is one of the main magic classes used in most known spells. It is used to
control the properties and the behaviour of the executed spell itself.
