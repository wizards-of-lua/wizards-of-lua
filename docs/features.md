---
title: Features
---
*The Wizards of Lua modification adds the spell entity and two new commands to Minecraft,
the lua command and the wol command.
For more information please read the [introduction](/introduction).*

The following features are already implemented in 1.1.0-alpha:

### Commands
* The [/lua command](/lua-command) casts a spell.
* The [/wol command](/wol-command) can list all active spells.
* The [/wol command](/wol-command) can break all active spells.
* The [/wol command](/wol-command) configure the maximum number of Lua ticks a spell can use per game tick.

### API
* A spell can create any available block using the [Blocks module](/modules/Blocks/).
* A spell can read and modify all block properties using the [Block class](/modules/Block/).
* A spell can copy and paste blocks using the [Spell.block](/modules/Spell/#block) property.
* A spell can read and modify a small portion of entity properties using the [Entity class](/modules/Entity/).
* A spell can be [moved](/modules/Entity/#move) around.
* A spell can be made [visible](/modules/Spell/#visible).
* A spell can [execute](/modules/Spell/#execute) any Minecraft command. This includes casting other spells.
* A spell can be sent to [sleep](/modules/Runtime/#sleep) for a certain amount of game ticks.
* A spell can read and write entity NBT data using [Entity.nbt](/modules/Entity/#nbt) and [Entity.putNbt()](/modules/Entity/#putNbt)
* A spell can query for entities using [Entities.find()](/modules/Entities/#find).

For a list of upcoming features please have a look at the [roadmap](/roadmap).
