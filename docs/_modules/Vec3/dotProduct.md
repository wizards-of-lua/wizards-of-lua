#### Example
Creating the dot-product of two vectors.
```lua
local p1 = Vec3( 5, 4, 3)
local p2 = Vec3( 2, 4, 6)
local p3 = p1:dotProduct( p2)
```
Since `dotProduct()` is also used in `__mul` of Vec3's metatable, you can
shorten the above example by using the `*` operator.
```lua
local p1 = Vec3( 5, 4, 3)
local p2 = Vec3( 2, 4, 6)
local p3 = p1 * p2
```
