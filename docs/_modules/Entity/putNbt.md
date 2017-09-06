#### Example
Setting the health of all bats to 1.
```lua
for _,e in Entities.find("@e[type=Bat]") do
  e:putNbt({Health=1})
end
```
