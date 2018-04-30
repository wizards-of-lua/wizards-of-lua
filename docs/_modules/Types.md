---
name: Types
subtitle: Managing Types
type: module
layout: module
properties:
functions:
  - name: declare
    parameters: name, superclass
    results: nil
    description: |
      The 'declare' function creates a new class with the given name and the optionally given superclass.

      #### Example
      Declaring a "Book" class with some functions.
      ```lua
      Types.declare("Book")

      function Book.new(title)
        local o = {title=title, pages={}}
        setmetatable(o,Book)
        return o
      end

      function Book:addPage(text)
        table.insert(self.pages, text)
      end
      ```

      Please note that there is also a shortcut for "Types.declare":
      ```lua
      declare("Book")
      ```

      #### Example
      Declaring the "Newspaper" class as a subclass of "Book".
      ```lua
      declare("Newspaper", Book)

      function Newspaper.new(title)
        local o = {title=title, pages={}}
        setmetatable(o,Newspaper)
        return o
      end
      ```
  - name: instanceOf
    parameters: class, object
    results: boolean
    description: |
      The 'instanceOf' function returns true if the given object is an instance of the given class.

      #### Example
      Checking if the current spell's owner is a player.
      ```lua
      if Types.instanceOf(Player, spell.owner) then
        print("Owner is a player")
      end
      ```
  - name: type
    parameters: object
    results: string
    description: |
      The 'type' function returns the name of the given object's type.

      #### Example
      Printing the type of the spell's owner.
      ```lua
      print( Types.type(spell.owner))
      ```
      Since "Types.type" is widely used there exists also a shortcut: "type".
      ```lua
      print( type(spell.owner))
      ```
---

The <span class="notranslate">Types</span> module can be used to check objects for their type and to create new types.
