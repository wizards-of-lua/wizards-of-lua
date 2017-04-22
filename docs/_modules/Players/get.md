#### Example
Create a function that returns a list of all all players currently logged in:
```lua
function listOfAllPlayers()
  local ids = Players.ids()
  local result = {}
  for _,id in pairs( ids) do
    local player = Players.get( id)
    table.insert( list, player)
  end
  return result
end
```
