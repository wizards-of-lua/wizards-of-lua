#### Example
Adding two vectors:
```lua
local p1 = vec3( 1, 0, 8)
local p2 = vec3( -1, 2, 3)
local p3 = p1:add( p2)
```
Since `add()` is also used as `__add` of Vec3's metatable, you can
shorten the above example by using the `+` operator:
```lua
local p1 = vec3( 1, 0, 8)
local p2 = vec3( -1, 2, 3)
local p3 = p1 + p2
```
#### Example
Getting the point in 5 meters distance where the current spell's owner is looking at:
```lua
local point = spell.owner.pos + vec3( 0, spell.owner.eyeHeight, 0) + spell.owner.lookVec * 5
```
