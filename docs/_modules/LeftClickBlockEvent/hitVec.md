#### Example
Creating some particle effect at the left-click hit position.
```lua
local queue = Events.collect("LeftClickBlockEvent")
while true do
  local event = queue:next()
  local v = event.hitVec
  spell:execute([[
    /particle angryVillager %s %s %s 0 0 0 0 1 true
  ]], v.x, v.y, v.z)
end
```
