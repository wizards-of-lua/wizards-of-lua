#### Example
Rewarding a player who died in lava with a brand new lava bucket.
```lua
local causes = {}
local queue = Events.connect("LivingDeathEvent","PlayerRespawnEvent")
while true do
  local event = queue:next()
  if event.name == "LivingDeathEvent" and type(event.entity)=="Player" then
    causes[event.entity.name] = event.cause
  elseif event.name == "PlayerRespawnEvent" then
    if causes[event.player.name]=="lava" then
      spell:execute("/give %s minecraft:lava_bucket", event.player.name)
    end
  end
end
```
