---
title: The Lua Command
---
*The <tt>/lua</tt>&nbsp;command is used to cast a [spell](/introduction) entity in Minecraft.
This command can be executed from the in-game chat and from any command block.*

When executed, the Lua command casts a new spell.
A spell is a game entity that exists at a specific location inside the world.
It will exist as long as its program is running.
After the program has terminated the spell vanishes from the world.

To cast a spell, the player must be a member of the Wizards of Lua.
Currently (as with the alpha version) only a player with operator priviliges is a wizard.
In a single player game the "allow cheats" option must be active to allow the player to
be a wizard.

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
After this, the spell will terminate.
The word <tt>print</tt> is the name of a function of a standard Lua module.

Besides standard functions you can also use functions, modules and
classes that come with the "Wizards of Lua" modification.

## Building a Pillar of Blocks
A detailed documentation of mod-specific functions, modules and
classes is available at the [Spell Book Library](/spellbooklibrary.html).
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

For more examples please have a look at the [Spell Book Library](/spellbooklibrary.html)
and the [tutorials](/tutorials.html).

## Importing Lua Modules from the Filesystem
Since writing a Lua program into the chat is inconvenient and only works until the character limited of 256 is reached,
it makes sense to put it into a file and load it later on request.
Luckily WoL comes with a built-in Lua code editor to support file editing.
To open the editor you can use the [/wol file edit](/wol-command.html#Personal-Files) command.

Let's consider you have created a file called "my-module.lua" in your personal directory with the following contents:
```lua
function myfunc()
  print("you just called myfunc")
end
```
If you want to call ```myfunc()``` in a spell, you have to "require" this module before you can call the function:
```lua
/lua require "my-module"; myfunc()
```

## Using a Personal Profile
Your personal profile is a special file called "profile.lua" that exists in your personal directory.
If it exists, and if you cast a spell from the chat, then your profile is automatically required by your spell.

For example, you can get rid of the "require" part above, if you put the following call into a file called "profile.lua" inside you personal directory:
```lua
require "my-module"
```

Then you can use ```myfunc()``` like this:
```lua
/lua myfunc()
```
