#### Example
Set the game time to 'day'.
```lua
spell:execute( "time set day")
```
#### Example
Spawn a zombie.
```lua
spell:move( "UP")
spell:execute( "summon Zombie ~ ~ ~")
```
#### Example
Build a wall by spawning some parallel spells each building a pillar:
```lua
for x=1,20 do
  spell:execute( 'lua for i=1,30 do spell.block="stone"; sleep(1); spell:move("UP"); end')
  spell:move( "RIGHT")
end
```
