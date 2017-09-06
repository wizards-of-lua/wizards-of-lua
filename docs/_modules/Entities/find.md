#### Example
Printing the number of all [Players](/module/Player) currently logged in.
```lua
found = Entities.find("@e[type=Player]")
print(#found)
```

#### Example
Printing the positions of all cows loaded in this world.
```lua
found = Entities.find("@e[type=Cow]")
for _,cow in pairs(found) do
  print(cow.pos)
end
```

#### Example
Printing the names of all dropped items loaded in this world.
```lua
found = Entities.find("@e")
for _,e in pairs(found) do
  if e.name:match("item%.item%.") then
    print(e.name)
  end
end
```
