---
name: Entities
title: Entities
subtitle: Finding Entities
type: module
layout: module
properties:
functions:
  - name: find
    parameters: selector
    results: 'table'
    description: |
        The ‘find’ function returns a table of Entity objects that match the given selector.
       
        #### Example
       
        Printing the number of all players currently logged in.
       
        ```lua
        found = Entities.find("@a")
        print(#found)
        ```
        
        #### Example
       
        Printing the position of player mickkay.
       
        ```lua
        found = Entities.find("mickkay")[1]
        print(found.pos)
        ```
       
        #### Example
       
        Printing the positions of all cows in the (loaded part of the) world.
       
        ```lua
        found = Entities.find("@e[type=cow]")
        for _,cow in pairs(found) do
          print(cow.pos)
        end
        ```
       
        #### Example
       
        Printing the names of all dropped items in the (loaded part of the) world.
       
        ```lua
        found = Entities.find("@e[type=item]")
        for _,e in pairs(found) do
          print(e.name)
        end
        ```
  - name: summon
    parameters: nbt
    results: '[Entity](/modules/Entity)'
    description: |
        The ‘summon’ function returns a freshly created entity of the given type, having the optionally
        given Nbt values.
       
        #### Example
       
        Creating a pig and moving it half a meter upwards.
       
        ```lua
        pig = Entities.summon("pig")
        pig:move("up",0.5)
        ```
        
        #### Example
       
        Creating a creeper with no AI.
       
        ```lua
        Entities.summon("creeper", {NoAI=1})
        ```
       
        #### Example
       
        Creating a zombie with no AI that is spinning around.
       
        ```lua
        z = Entities.summon("zombie", {NoAI=1})
        while true do
          z.rotationYaw = z.rotationYaw + 10
          sleep(1)
        end
        ```
---

The <span class="notranslate">Entities</span> module provides access to all
[Entity](/module/Entity) objects currently loaded.
