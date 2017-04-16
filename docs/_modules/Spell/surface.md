#### Example
Print the spell's surface:
```lua
print( spell.surface )
```
#### Example
Copy the block at the spell's position 3 times into the direction of the
spell's surface:
```lua
if spell.surface ~= nil then
  local copy = spell.block
  for i=1,3 do
    move(spell.surface)
    spell.block = copy
  end
end
```
