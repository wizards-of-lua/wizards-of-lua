#### Example
Find all players in a radius of 10 meters around the spell's position and print
their IDs:
```lua
local ids = Players.find("@p[r=10]")
for _,id in pairs(ids) do
  print(id)
end
```
