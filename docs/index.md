---
page.title: The Art of Spell Crafting
---
# Welcome to the Home of the Wizards of Lua

![Manison](images/manison.jpg)

The Wizards of Lua are a community of programmers and their friends
who want to spread the knowlede of programming to all people.

The Wizards of Lua are also some decent gamers
who belive that interactive programming opens a wide channel to transform
the creativity of former plain consumers into a multitude of game contents
and experience.

And finally The Wizards of Lua is a Minecraft modification that simply adds
the `/lua` command to the game.

# The Lingua Magica
By installing 'Wizards of Lua' onto your Minecraft server, you are
changing the very nature of your Minecraft game: it becomes magical in a wordly
sense.
The mod makes Lua, a programming language, available from the in-game chat window,
so that that players can use it online to cast powerful 'spells' to control
almost any aspect of the world.

By using existing spell books ('function libraries') players can enjoy
completely new variants of the Minecraft game, without any need to understand
the underlying 'magic'.
But by studying Lua, the lingua magica, players can gain a deep knowledge about
how the magic works and how anybody can use little 'magic words'
to craft their own unique spells that can do things that were not possible
before.

# Casting a Spell
As any other Minecraft command you can execute it directly from the chat
window.
Just type 't' to open the chat and command prompt and begin typing: `/lua`.
Next to this command you can enter any sequence of statements that form
a valid Lua program.

For example, you could enter the following line:
```lua
/lua for i=1,10 do spell.block = "stone"; spell:move("UP"); end
```
This will cast a spell that creates a pillar of stones directly in front of you,
10 meters tall. Here is a picture of it:

![Pillar of Stone](images/pillar-of-stone.jpg)

The base of the pillar is right at that location you where looking at when you
started to type the command.

But changing the world by adding (or removing) blocks is not the only thing
you can do with Lua.
To find out what can be done just have a look at the Lua Spell Book Library.

# The Lua Spell Book Library
The Wizards of Lua use magic words as their raw material to craft powerful spells.
The Lua Spell Book Library contains a collection of books that list all basic
magic words that have been discovered, or invented, by the very first members,
called archmages, so far.
There are, almost certainly, more words out there, but to begin with, this library
is a good starting point.
Every magic word is documented within its own section.
Next to its name you find a brief description about its main effects and, if
available, about the options you have to influence these effects.

{% assign modules = site.modules %}
{% for module in modules %}
* <a href="{{ module.relative_path }}">{{ module.name }}: {{ module.title }}</a>

{% endfor %}
