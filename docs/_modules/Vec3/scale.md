#### Example
Scaling a vector by 3:
```lua
local p1 = vec3( 11, 22, 33)
local p2 = p1:scale( 3)
```
Since `scale()` is also used as `__mul` of Vec3's metatable, you can
shorten the above example:
```lua
local p1 = vec3( 11, 22, 33)
local p2 = p1 * 3
```
