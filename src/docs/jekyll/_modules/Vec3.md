---
name: Vec3
subtitle: Manipulating Location and Motion
type: class
layout: module
properties:
  - name: x
    type: number
    access: r/w
    description: The X-component of the vector
  - name: y
    type: number
    access: r/w
    description: The Y-component of the vector
  - name: z
    type: number
    access: r/w
    description: The Z-component of the vector
functions:
  - name: add
    parameters: other
    results: Vec3
    description: |
        Returns a new vector that is the result of adding the other vector to the current vector.

        #### Example

        Adding two vectors.

        ```lua
        local p1 = Vec3( 1, 0, 8)
        local p2 = Vec3( -1, 2, 3)
        local p3 = p1:add( p2)
        ```

        Since `add()` is also used as `__add` of Vec3's metatable, you can shorten the above example by using the `+` operator.

        ```lua
        local p1 = Vec3( 1, 0, 8)
        local p2 = Vec3( -1, 2, 3)
        local p3 = p1 + p2
        ```
  - name: chunk
    parameters:
    results: number, number
    description: |
        The 'chunk' function interprets this vector as world coordinate, converts them into chunk coordinates, and returns them as a multi-value result.

        #### Example

        Converting the spell's world coordinates into chunk coordinates.

        ```lua
        local chunkX, chunkZ = spell.pos:chunk()
        ```
  - name: dotProduct
    parameters: other
    results: Vec3
    description: |
        Returns the 'dot' product of the current vector and the other vector.

        #### Example

        Creating the dot-product of two vectors.

        ```lua
        local p1 = Vec3( 5, 4, 3)
        local p2 = Vec3( 2, 4, 6)
        local p3 = p1:dotProduct( p2)
        ```

        Since `dotProduct()` is also used in `__mul` of Vec3's metatable, you can shorten the above example by using the `*` operator.

        ```lua
        local p1 = Vec3( 5, 4, 3)
        local p2 = Vec3( 2, 4, 6)
        local p3 = p1 * p2
        ```
  - name: invert
    parameters:
    results: Vec3
    description: |
        Returns an inverted version of the current vector.

        #### Example

        Inverting a vector.

        ```lua
        local p1 = Vec3( 5, 2 , -1)
        local p2 = p1:invert()
        ```

        Since `invert()` is also used as `__unm` of Vec3's metatable, you can shorten the above example.

        ```lua
        local p1 = Vec3( 5, 2 , -1)
        local p2 = - p1
        ```
  - name: magnitude
    parameters:
    results: Vec3
    description: |
        Returns the length of the current vector.

        #### Example

        Getting the length of a vector.

        ```lua
        local p = Vec3( 2, 3, 4)
        local len = p:magnitude()
        ```
  - name: normalize
    parameters:
    results: Vec3
    description: |
        Returns a normalized version of the current vector, which means a vector with a magnitude of 1 meter and pointing into the same direction as the original vector.

        #### Example

        Normalizing a vector.

        ```lua
        local vec = Vec3( 2, 3, 4)
        local normalizedVec = vec:normalize()
        ```
  - name: scale
    parameters: factor
    results: Vec3
    description: |
        Returns a copy of current vector, scaled by the given factor.

        #### Example

        Scaling a vector by 3.

        ```lua
        local p1 = Vec3( 11, 22, 33)
        local p2 = p1:scale( 3)
        ```

        Since `scale()` is also used as `__mul` of Vec3's metatable, you can shorten the above example.

        ```lua
        local p1 = Vec3( 11, 22, 33)
        local p2 = p1 * 3
        ```
  - name: sqrMagnitude
    parameters:
    results: Vec3
    description: |
        Returns the squared length of the current vector.

        #### Example

        Getting the 'squared' length of a vector.

        ```lua
        local p = Vec3( 2, 3, 4)
        local slen = p:sqrMagnitude()
        ```
  - name: substract
    parameters: other
    results: Vec3
    description: |
        Returns a new vector that is the result of substracting the other vector from the current vector.

        #### Example

        Substracting two vectors.

        ```lua
        local p1 = Vec3( 5, 4, 3)
        local p2 = Vec3( 2, 4, 6)
        local p3 = p1:substract( p2)
        ```

        Since `substract()` is also used as `__sub` of Vec3's metatable, you can shorten the above example by using the `-` operator.

        ```lua
        local p1 = Vec3( 5, 4, 3)
        local p2 = Vec3( 2, 4, 6)
        local p3 = p1 - p2
        ```
  - name: tostring
    parameters:
    results: string
    description: |
        Returns a string with the following format '(x, y, z)'.

        #### Example

        Printing the vector using tostring().

        ```lua
        local p = Vec3( 11, 22, 33)
        print( p:tostring() )
        ```

        Since `tostring()` is also used as `__tostring` of Vec3's metatable, you can shorten the above example.

        ```lua
        local p = Vec3( 11, 22, 33)
        print( p)
        ```
---

An instance of the <span class="notranslate">Vec3</span> class represents a '3-dimensional Vector'.

Mostly a 3-dimensional vector is used to denote a position in the
3-dimensional world space or a constant velocity of an object inside that space.

However, a vector can be used for many other 'things' that can be described by
3 independent numerical values.

To create a vector you can call the <span class="notranslate">Vec3</span> function:
```lua
myvec = Vec3( 1, 2, 3)
```
This creates a vector called 'myvec' with the component values x=1, y=2, z=3.
