#### Example
Print the name of the block at the spell's position:
```lua
print( spell.block.name )
```
#### Example
Change the block at the spell's position to dirt:
```lua
spell.block = "dirt"
```
#### Example
Copy the block at the spell's position 10 times upwards:
```lua
local copy = spell.block
for i=1,10 do
  move(UP)
  spell.block = copy
end
```
