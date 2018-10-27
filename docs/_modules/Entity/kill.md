#### Example
Killing all pigs that are swimming in liquid material.
```lua
local pigs = Entities.find("@e[type=pig]")
for _,pig in pairs(pigs) do
  spell.pos = pig.pos
  if spell.block.material.liquid then
    pig:kill()
  end
end
```
