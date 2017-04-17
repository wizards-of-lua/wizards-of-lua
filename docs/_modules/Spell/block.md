#### Example
Print the name of the block at the spell's position:
```lua
print( spell.block.name )
```
#### Example
Inspect the block state the spell's position:
```lua
print( inspect( spell.block))
```
#### Example
Change the block at the spell's position into dirt:
```lua
spell.block = "dirt"
```
#### Example
Copy the block at the spell's position 10 times upwards:
```lua
local copy = spell.block
for i=1,10 do
  spell:move("UP")
  spell.block = copy
end
```
