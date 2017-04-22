#### Example
Printing the names of all players currently logged in:
```lua
local names = Players.names()
for _,name in pairs(names) do
  print(name)
end
```
