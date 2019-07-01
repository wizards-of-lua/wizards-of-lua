---
title:  "Alpha 1.2.0 is Released"
date:   2017-09-06 15:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The new 1.2.0-alpha contains two new features.
Now you can query for entities, and you can modify the nbt data of an entity.
<!--more-->

* Fixed [#36](https://github.com/wizards-of-lua/wizards-of-lua/issues/36): Spells should be able to read and write entity NBT data. See the [Entity Class](/versions/current/modules/Entity) for details.
* Fixed [#37](https://github.com/wizards-of-lua/wizards-of-lua/issues/37): Spells should be able to query for entities. See the [Entities Module](/versions/current/modules/Entities) for details.

### Example: Querying Entities
This will print the number of all players currently logged in:
```lua
found = Entities.find( "@e[type=Player]")
print( #found)
```


### Example: Reading Entity NBT data
This will print the NBT data of the spell's owner:
```lua
print( str( spell.owner.nbt))
```

### Example: Modifying Entity NBT data
Setting the health of all bats to 1.
```lua
for _,e in Entities.find("@e[type=Bat]") do
  e:putNbt({Health=1})
end
```
