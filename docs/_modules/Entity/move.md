#### Example
Moving the spell 1 meter upwards.
```lua
spell.move( "up")
```

#### Example
Moving the spell 10 meters to the north.
```lua
spell.move( "north", 10)
```

#### Example
Moving the spell's owner for half a meter to the left.
```lua
spell.owner:move( "left", 0.5)
```

#### Example
Building a huge circle of wool blocks.
```lua
wool=Blocks.get( "wool")
for i=1,360 do
  spell.block=wool
  spell.rotationYaw=spell.rotationYaw+1
  spell:move("forward")
end
```
