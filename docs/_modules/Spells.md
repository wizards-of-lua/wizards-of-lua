---
name: Spells
subtitle: Finding Spells
type: module
layout: module
properties:
functions:
  - name: find
    parameters: criteria
    results: table
    description: |
        The 'find' function returns a table of [Spell](/modules/Spell/) objects that match the given criteria.
        The criteria is a table of key-value pairs.

        The following keys are supported:
        * name : string
        * owner : string
        * tag : string
        * sid : number
        * maxradius: number
        * minradius: number
        * excludeSelf: boolean

        #### Example
        Printing the number of all active [spells](/modules/Spell).
        ```lua
        found = Spells.find()
        print(#found)
        ```

        #### Example
        Printing the position of the first [spell](/modules/Spell) called "welcome":
        ```lua
        found = Spells.find({name="welcome"})[1]
        print(found.pos)
        ```

        #### Example
        Printing the positions of all active spells in a 10-meter range.
        ```lua
        found = Spells.find({maxradius=10})
        for _,s in pairs(found) do
          print(s.pos)
        end
        ```

        #### Example
        Printing the names of all active spells owned by player "mickkay".
        ```lua
        found = Spells.find({owner="mickkay"})
        for _,e in pairs(found) do
          print(e.name)
        end
        ```

---

The <span class="notranslate">Spells</span> module provides access to all [Spell](/modules/Spell/) objects currently loaded.
