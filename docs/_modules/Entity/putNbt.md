#### Example
Cutting the health of all bats to half.
```lua
local e = Entities.find("@e[type=bat]")
for _,bat in pairs(e) do
  local h=math.floor(bat.nbt.Health/2)
  bat:putNbt({Health=h})
  print(bat.nbt.Health)
end
```

#### Example
Finding all pigs and putting a saddle on each of them.
```lua
for _,pig in pairs(Entities.find("@e[type=pig]")) do
  pig:putNbt({Saddle=1})
end
```
