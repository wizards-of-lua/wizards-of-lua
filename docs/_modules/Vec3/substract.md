#### Example
Substracting two vectors:
```lua
local p1 = Vec3( 5, 4, 3)
local p2 = Vec3( 2, 4, 6)
local p3 = p1:substract( p2)
```
Since `substract()` is also used as `__sub` of Vec3's metatable, you can
shorten the above example by using the `-` operator:
```lua
local p1 = Vec3( 5, 4, 3)
local p2 = Vec3( 2, 4, 6)
local p3 = p1 - p2
```
