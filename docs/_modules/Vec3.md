---
title: Using Vector Math to Manipulate Location and Motion
name: Vec3
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
  - name: tostring
    parameters:
    results: string
    description: Returns a string with the following format '(x, y, z)'.
    examples:
      - url: Vec3/tostring.md
  - name: add
    parameters: other
    results: Vec3
    description: Returns a new vector that is the result of adding the other vector to the current vector.
    examples:
      - url: Vec3/add.md
  - name: substract
    parameters: other
    results: Vec3
    description: Returns a new vector that is the result of substracting the other vector from the current vector.
    examples:
      - url: Vec3/substract.md
  - name: sqrMagnitude
    parameters:
    results: Vec3
    description: Returns the squared length of the current vector.
    examples:
      - url: Vec3/sqrMagnitude.md
  - name: magnitude
    parameters:
    results: Vec3
    description: Returns the length of the current vector.
    examples:
      - url: Vec3/magnitude.md
  - name: dotProduct
    parameters: other
    results: Vec3
    description: Returns the 'dot' product of the current vector and the other vector.
    examples:
      - url: Vec3/dotProduct.md
  - name: scale
    parameters: factor
    results: Vec3
    description: Returns a scaled version of the current vector by the given factor.
    examples:
      - url: Vec3/scale.md
  - name: invert
    parameters:
    results: Vec3
    description: Returns an inverted version of the current vector.
    examples:
      - url: Vec3/invert.md
---
{% include module-head.md %}

'Vec3' is short for '3-dimensional Vector'.
Mostly a 3-dimensional vector is used to denote a position in the
3-dimensional world space. And sometimes it is used to denote a constant velocity
of an object inside that space.
Actually a vector can be used for many other 'things' that can be described by
3 independent numerical values.

To create a vector you can call the Vec3 function:
```lua
/lua myvec = Vec3( 1, 2, 3)
```
This creates a vector called 'myvec' with the component values x=1, y=2, z=3.

{% include module-body.md %}
