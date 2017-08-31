---
title: Introduction
---
*The Wizards of Lua modification adds the Lua programming language to the Minecraft game.
By using it you can change the nature of the game.*

The Wizards of Lua modification adds a new entity, called the Spell entity, to Minecraft.
A spell is a (normally) invisible entity that can be casted by a player using the
[/lua command](/lua-command).
Like any other entity each spell exists at a specific location
(defined by a 3-dimensional [vector](/modules/Vec3/)) inside a specific dimension
(like the Overworld or the Nether).
The special thing about a spell is that it contains a sequence of magic words which
define exactly how this spell behaves in the world.
These words belong to a special language, which is called Lua, the 'lingua magica', meaning the
language of magic.
Lua is actually a popular [programming language](https://www.lua.org).
Therefore a spell is in fact a little computer program, and a wizard is a software
developer.

To interact with the Minecraft world, a spell can make use of a special application
programming interface (API).
This api is documentend by a bunch of small books which are collected inside the
[spell book library](/spellbooklibrary).

By studying the [examples](/examples.html) and [tutorials](/tutorials.html),
players can gain a deep knowledge about how the linuga magica works and become
themselves great wizards.

## Casting a Spell
A wizard can cast a spell using the [/lua command](/lua-command).
As any other Minecraft command it can be executed directly from the chat
window (or from within a command block).

For example, to execute a spell from the chat, just type 't' to open the chat
prompt and begin typing:
```
/lua
```

Next to this command you can enter any sequence of statements that form
a valid Lua program.


For example, you could enter the following line into the chat:
```lua
/lua for i=1,10 do spell.block=Blocks.get("stone"); spell:move("up"); end
```
This will cast a spell that creates a pillar of stones directly in front of you,
10 meters tall. Here is a picture of it:

![Pillar of Stone](images/pillar-of-stone.jpg)

The base of the pillar is right at that location you where looking at when you
submitted the command.

But changing the world by adding (or removing) blocks is not the only thing
you can do with Lua.
To find out what can be done just have a look at the [Lua Spell Book Library](/spellbooklibrary),
the [tutorials](/tutorials.html), and our [list of shared spells](/spells.html).
