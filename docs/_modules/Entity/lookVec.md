#### Example
Letting the wizard spit 10 meters into the direction he is looking at.
```lua
dir=spell.owner.lookVec
s=spell.owner.pos+Vec3(0,spell.owner.eyeHeight,0)
for i=1,10,0.1 do
  spell.pos=s+dir*i
  spell:execute("particle droplet ~ ~ ~ 0 0 0 0")
end
```

#### Example
Moving the spell into the owners eye and pointing it into the owner's look direction.
```lua
spell.pos = spell.owner.pos+Vec3(0,spell.owner.eyeHeight,0);
spell.lookVec = spell.owner.lookVec
```
