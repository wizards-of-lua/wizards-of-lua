#### Example
Drop a number of diamonds equal to the amount of experience a player gains through mining coal ore, redstone ore, etc. Note that you don't get experience from breaking blocks in creative mode.
```lua
local queue = Events.collect("BlockBreakEvent")
while true do
  local event = queue:next()
  spell.pos = event.pos
  if event.experience > 0 then
    spell:dropItem(Items.get('diamond', event.experience))
  end
end
```
