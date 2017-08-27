---
title: Introduction
---
By installing the ['Wizards of Lua' mod](https://github.com/wizards-of-lua/wizards-of-lua)
onto your Minecraft server, you are changing the nature of your Minecraft
game: it becomes magical in a wordly sense.
This mod transforms [Lua, the programming language](https://www.lua.org),
into a 'lingua magica', a language of magic, players can use it to create and cast powerful 'spells' to control almost any aspect of the world.
Lua can be executed from the in-game chat window and from command blocks.

By using existing function libraries (so called 'spell books') players can enjoy
completely new variants of the Minecraft game without great effort.
And by studying the examples found inside the spell books,
players can gain a deep knowledge about how the linuga magica works
and how anybody can use little 'magic words' to craft their own unique spells
in order to do things that were not possible before.

## Casting a Spell
As any other Minecraft command you can execute it directly from the chat
window.
Just type 't' to open the command prompt and begin typing:
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
To find out what can be done just have a look at the [Lua Spell Book Library](spellbooklibrary.md).
