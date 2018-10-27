#### Example
Putting on a helmet on all zombies.
```lua
for _,zombie in pairs(Entities.find("@e[type=zombie]")) do
  local n=zombie.nbt
  n.ArmorItems[4]={Count=1,id="iron_helmet"}
  zombie:putNbt(n)
end
```
