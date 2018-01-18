---
title: The Wizards of Lua Configuration File
---
*The Wizards of Lua Mod can be configured by editing its configuration file.*

The Wizards of Lua configuration file is located at

    <minecraft>/conf/wizards-of-lua/wizards-of-lua.luacfg

where &lt;minecraft&gt; is your Minecraft folder.

Please note that you should only edit it manually when Minecraft is not running.
Otherwise your changes will be ignored and might get overwritten when the mod's configuration has been changed by some [/wol command](/wol-command.html).

## Example
Here is an example of the <tt>wizards-of-lua.luacfg</tt>:
```lua
General {
  luaTicksLimit=10000,
  showAboutMessage=true,
  luaLibDirHome="libs",
  sharedLibDir="shared" }
Wizard {
  id="0c77f1a8-943b-4f7b-aa09-f2b1737d4f03",
  libDir="0c77f1a8-943b-4f7b-aa09-f2b1737d4f03" }
Wizard {
  id="d26feccb-baae-4d90-8e3d-50389e8b8ad9",
  libDir="d26feccb-baae-4d90-8e3d-50389e8b8ad9" }
```

## Format
The contents of <tt>wizards-of-lua.luacfg</tt> is actually valid Lua code which is executed by Wol at server startup.
"General" and "Wizard" are two internal functions that accept a Lua table as parameter.

### General
This function sets the mod's general configuration.
* **luaTicksLimit**: defines the number of Lua ticks a spell can run continuously before it must sleep at least for one game tick.
This value can be modified from within the game by [/wol luaTicksLimit](/wol-command.html#Lua-Ticks-Limit).
* **showAboutMessage**: defines, whether the mod's about message is shown to new users when they log into the server.
Please note, that until the next server restart the about message is only shown once to each user.
* **luaLibDirHome**: defines the relative or absolute location of the home of all Lua library directories.
This directory is parent to the shared lib and all user-specific library directories.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).
* **sharedLibDir**: defines the relative or absolute location of the shared library directory.
This directory is parent of all Lua modules that can be used by all spell.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).

### Wizard
This function adds a player-specific configuration.
New entries are added automatically when a new player uses the [/lua command](/lua-command.html) or the [/wol command](/wol-command.html) the first time.
* **id**: is the UUID of the player this configuration belongs to.
* **libDir**: defines the relative or absolute location of the player-specific Lua library directory.
This directory is parent of all Lua modules that can be used by the player.
If defined as relative, it will be located inside the directory defined by <tt>General.luaLibDirHome</tt>.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).
