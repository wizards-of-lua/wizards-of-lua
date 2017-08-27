#### Example
Setting the game time to 'day'.
```lua
spell:execute( "time set day")
```
#### Example
Spawning a zombie at the spell's current location:
```lua
spell:move( "up")
spell:execute( "summon Zombie ~ ~ ~")
```
#### Example
Spawning some smoke particles at the spell's current location:
```lua
local particle = "smoke"
spell:execute( "particle %s ~ ~ ~ 0 0 0 0 0", particle)
```
#### Example
Building a wall by casting some parallel spells each building a pillar:
```lua
for x=1,20 do
  spell:execute(
    'lua for i=1,5 do spell.block = Blocks.get( "stone"); sleep( 1); spell:move( "up"); end'
  )
  spell:move( "north")
end
```
#### Example
Drawing a circular could of black smoke into the air at the spell's position.
```lua
start = spell.pos
for a=0,math.pi*2,0.1 do
  z = math.sin( a)
  x = math.cos( a)
  y = 0.6
  r = 1.4
  spell.pos = start + Vec3( x, y, z) * r
  spell:execute( "particle largesmoke ~ ~ ~ 0 0 0 0 1")
end
```
