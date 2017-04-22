#### Example
Printing the IDs of all players currently logged in:
```lua
local ids = Players.ids()
for _,id in pairs(ids) do
  print(id)
end
```
