#### Example
Finding all pigs and putting a saddle on them.
```lua
for _,p in pairs(Entities.find("@e[type=Pig]")) do
  nbt=p.nbt
  nbt.Saddle=1
  p:putNbt(nbt)
end
```
