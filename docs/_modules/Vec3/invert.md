#### Example
Inverting a vector:
```lua
local p1 = Vec3( 5, 2 , -1)
local p2 = p1:invert()
```
Since `invert()` is also used as `__unm` of Vec3's metatable, you can
shorten the above example:
```lua
local p1 = Vec3( 5, 2 , -1)
local p2 = - p1
```
