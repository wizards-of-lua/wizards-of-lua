#### Example
Making the spell visible.
```lua
spell.visible = true
```

#### Example
Making the spell visible and moving it around in a circle.
```lua
spell.visible = true
start = spell.pos
for a=0,math.pi*2,0.1 do
  z = math.sin( a)
  x = math.cos( a)
  r = 3  
  spell.pos = start + Vec3( x, 0, z) * r
  sleep( 1)
end
```
