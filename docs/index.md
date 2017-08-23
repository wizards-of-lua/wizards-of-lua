---
title: The Art of Spell Crafting
---
# Welcome to the Home of the Wizards of Lua

![Manison](images/manison.jpg)

The Wizards of Lua are a [community of programmers and their friends](members.md),
who want to spread the knowledge of programming.

The Wizards of Lua are also some decent gamers
who want to give gamers the ability to create their own game contents.

And finally 'The Wizards of Lua' is the name of a [Minecraft](https://minecraft.net/de-de/)
modification that simply adds the `/lua` command to the game.

# The Lingua Magica
By installing the ['Wizards of Lua' mod](https://github.com/wizards-of-lua/luamod)
onto your Minecraft server, you are changing the nature of your Minecraft
game: it becomes magical in a wordly sense.
This mod transforms [Lua, the programming language](https://www.lua.org),
into a 'lingua magica', a language of magic, which can be used from the in-game chat window
or from command blocks.
Players can use it to create and cast powerful 'spells' to control almost any
aspect of the world.

By using existing function libraries (so called 'spell books') players can enjoy
completely new variants of the Minecraft game without great effort.
And by studying the examples found inside the spell books,
players can gain a deep knowledge about how the linuga magica works
and how anybody can use little 'magic words' to craft their own unique spells
in order to do things that were not possible before.

# Casting a Spell
As any other Minecraft command you can execute it directly from the chat
window.
Just type 't' to open the command prompt and begin typing: `/lua`.
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
To find out what can be done just have a look at the Lua Spell Book Library.

# The Lua Spell Book Library
The Wizards of Lua use magic words as ingredients to craft powerful spells.
The Lua Spell Book Library contains a collection of books that list all basic
magic words that have been discovered, or invented, by the very first members,
called archmages, so far.
There are a lot of different categories of magic words:
keywords, modules, types, properties, and functions.
The books listed below cover all those categories, but not all words completely.
There are, almost certainly, more words out there, but to begin with, this library
is a good starting point.

![Library of Lua](images/library-of-lua.jpg)

Below you find a list of books that are organized by modules and types.
Every type and module is documented within its own book.
Inside each book you will find sections about properties and functions.
In each of those there is a brief description about their nature, their effects,
and, if available, about the options you have to influence these effects.

To make it easy for the reader, the authors have added some small examples to
each section, that can be used as recipes for crafting spells.

This list is sorted alphabetically.

{% assign modules = site.modules | where_exp:"m", "m.title != 'TODO'" %}
{% assign modulesAvail = modules | sort: 'name' %}
{% for module in modulesAvail %}
* <a href="{{ module.url }}">{{ module.name }}: {{ module.title }}</a>
{% endfor %}
