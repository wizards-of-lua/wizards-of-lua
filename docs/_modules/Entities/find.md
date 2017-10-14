#### Example
Printing the number of all [players](/module/Player) currently logged in.
```lua
found = Entities.find("@e[type=player]")
print(#found)
```

#### Example
Printing the position of [player](/module/Player) mickkay:
```lua
found = Entities.find("@e[type=Player,name=mickkay]")[1]
print(found.pos)
```

#### Example
Printing the positions of all cows loaded in this world.
```lua
found = Entities.find("@e[type=cow]")
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
