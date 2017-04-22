#### Example
Setting the game time to 'day'.
```lua
spell:execute( "time set day")
```
#### Example
Spawning a zombie at the spell's current location:
```lua
spell:move( "UP")
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
  spell:execute( 'lua for i=1,30 do spell.block="stone"; sleep(1); spell:move("UP"); end')
  spell:move( "RIGHT")
end
```
