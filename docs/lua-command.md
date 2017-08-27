---
title: The Lua Command
---
# The Lua Command
The Wizards of Lua modification adds the <tt>/lua</tt>&nbsp;command to the Minecraft game.
This command can be executed from the in-game chat and from any command block.

To do this, the player must be a member of the Wizard of Lua.
Currently (as in Alpha 1.0.0) only players with operator priviliges are Wizards.
In a single player game the "allow cheats" option must be active to allow the player to
be a wizard.

When executed, the Lua command casts a new spell.
A spell is a game entity that exists at a specific location inside the world.
It will exist as long as its program is running.
After the program has terminated the spell vanishes from the world.

Due to the fact that this mod is based on the Forge mod, a maximum number of
200 spells can be active concurrently.
However, this number can be increased easily by editing the <tt>forgeChunkLoading.cfg</tt>
file, which is located inside the <tt>config</tt> directory inside your Minecraft installation.

Casting a spell is very easy. Here is a list of very short tutorials that help you
getting started.

## Casting a Spell
To cast a spell just type 't' to open the chat prompt and begin typing:
```
/lua
```
Next to this command you can enter any sequence of statements that form
a valid Lua program.
If you are new to Lua programming, please make yourself familiar with the basic
concepts of Lua.
There are many websites which do a good job in teaching Lua to the newcomer.
For example you could start with [www.lua.org](https://www.lua.org/start.html).

After this is said, you could enter the following line into the chat:
```lua
/lua print( 1+1)
```
This will cast a spell that prints the following line into your chat output:
```lua
2
```
After that the spell will terminate.
The word <tt>print</tt> is the name of a function of a standard Lua module.

Besides standard functions you can also use functions, modules and
classes that come with the "Wizards of Lua" modification.

## Building a Pillar of Blocks
A detailed documentation of mod-specific functions, modules and
classes is available at the [Spell Book Library](/spellbooklibrary/).
For now, we will grep a bunch of them in order to create a tall stone pillar.
For example, you could enter the following line into the chat:
```lua
/lua for i=1,10 do spell.block=Blocks.get("stone"); spell:move("up"); end
```
This will cast a spell that creates a pillar of stones directly in front of you,
10 meters tall. Here is a picture of it:

![Pillar of Stone](images/pillar-of-stone.jpg)

The base of the pillar is right at that location you where looking at when you
submitted the command.




For more examples please have a look at the [Spell Book Library](/spellbooklibrary/).
You also could come back and check this website frequently for other tutorials.
We are definitely planning to publish some more.
