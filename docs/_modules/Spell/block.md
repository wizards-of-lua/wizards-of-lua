#### Example
Printing the name of the block at the spell's position:
```lua
print( spell.block.name )
```
#### Example
Inspecting the block state the spell's position:
```lua
print( inspect( spell.block))
```
#### Example
Changing the block at the spell's position into dirt:
```lua
spell.block = "dirt"
```
#### Example
Copying the block at the spell's position 10 times upwards:
```lua
local copy = spell.block
for i=1,10 do
  spell:move("UP")
  spell.block = copy
end
```
