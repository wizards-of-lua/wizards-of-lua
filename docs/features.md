---
title: Features
---

The following features are already implemented in 1.1.0-alpha:

* Wizards can cast spells using the [/lua command](/lua-command).
* Wizards can list all active spells using the [/wol command](/wol-command).
* Wizards can break all active spells using the [/wol command](/wol-command).
* Spells can be casted by command blocks.
* Spells can create any available block using the [Blocks module](/modules/Blocks/).
* Spells can read and modify all block properties using the [Block class](/modules/Block/).
* Spells can copy and paste blocks using the [Spell.block](/modules/Spell/#block) property.
* Spells can read and modify a small amount of entity properties using the [Entity class](/modules/Entity/).
* Spells (in fact all entities) can be [moved](/modules/Entity/#move) around.
* Spells can be made [visible](/modules/Spell/#visible).
* Spells can [execute](/modules/Spell/#execute) any Minecraft command. This includes casting other spells.
* Spells can be sent to [sleep](/modules/Runtime/#sleep) for a certain amount of game ticks.
* Operators can configure the maximum number of Lua ticks a spell can use per game tick using the [/wol command](/wol-command).
