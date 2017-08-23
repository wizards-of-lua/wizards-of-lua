#### Example
Printing the vector using tostring():
```lua
local p = Vec3( 11, 22, 33)
print( p:tostring() )
```
Since `tostring()` is also used as `__tostring` of Vec3's metatable, you can
shorten the above example:
```lua
local p = Vec3( 11, 22, 33)
print( p)
```
