---
title: Importing Lua Files from the File System
excerpt_separator: <!--more-->
author: mickkay
level: 2
---
This tutorial shows how you can import external Lua files into your spells.
<!--more-->

Writing programs into the command line or a command block is inconvenient for programs with more the a few statements.
It is far better to use an text editor to edit Lua files, and then import them into your spells using the <tt>require</tt> function.

To view, create, and edit a file, you can use the [/wol file edit](/wol-command.html#Personal-Files) command.

### Importing a Personal Module
For each wizard there exists a directory where personal Lua files can be stored and which is automatically added to the Lua search path.

Let's assume that inside your personal directory there already exists a file called "<tt>utilities.lua</tt>" with the following contents:
```lua
function particle(name)
  spell:execute("particle "..name.." ~ ~ ~ 0 0 0 0 1")
end
```

If you want to use the <tt>particle</tt> function in your current spell, you can include your utilities module by using the <tt>require</tt> function.

```lua
/lua require("utilities"); particle("smoke")
```
Please note that you must omit the module's file extension ".lua".

If you want to always import a specific module for any of your spells, you might want to add it to your personal profile.

### Using a Personal Profile
A personal profile is an ordinary Lua file with the name "profile.lua" inside your directory.
If it exists, it is automatically required by any of your spells.

#### Example
Let's assume there is a file called "<tt>profile.lua</tt>" inside your directory with the following contents:

```lua
-- this is my profile
require "utilities"
```
Here this profile "requires" the "utilities" module from the above example.

Now you can call your <tt>particle</tt> function directly from any of your spells without the need to "require" it explicitly:
```lua
/lua particle("smoke")
```

## Location of your Personal Directory
By default your personal directory is found inside the Wol configuration directory at
    <minecraft>/config/wizards-of-lua/libs/<uuid>

where *&lt;minecraft&gt;* is the location of your Minecraft installation, and *&lt;uuid&gt;* is the UUID of the player.
You can change this location by editing the [configuration file](/configuration-file.html).

For example, on my Linux server the path to my player-specific Lua library is:

    /home/minecraftserver/config/wizards-of-lua/libs/0c77f1a8-943b-4f7b-aa09-f2b1737d4f03/

Or, when playing single-player on my Mac, the path is:

    ~/Library/Application Support/minecraft/config/
    wizards-of-lua/libs/0c77f1a8-943b-4f7b-aa09-f2b1737d4f03/
