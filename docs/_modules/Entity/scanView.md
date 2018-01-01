#### Example
Prints the name of the block the spell's owner is looking at (up to a maximum distance of 10 meters).

```lua
maxDistance = 10
hit = spell.owner:scanView( maxDistance)
if hit then
  spell.pos = hit.pos
  print(spell.owner.name.." is looking at "..spell.block.name)
end
```
