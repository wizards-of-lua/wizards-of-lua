---
title: Importing Lua Files from the File System
excerpt_separator: <!--more-->
author: mickkay
---
This tutorial shows how you can import external Lua files into your spells.
<!--more-->

Writing programs into the command line or a command block is inconvenient for programs with more the a few statements.
It is far better to use an external text editor to edit Lua files, and then import them into your spells using the <tt>require</tt> function.

## Using the Player-specific Library Directory
For each wizard there exists a directory where player-specific Lua files can be stored.
This directory is automatically added to the Lua search path.

By default this directory is found inside the Wol configuration directory at

    <minecraft>/config/wizards-of-lua/libs/<uuid>

where *&lt;minecraft&gt;* is the location of your Minecraft installation, and *&lt;uuid&gt;* is the UUID of the player.
You can change this location by editing the [configuration file](/configuration-file.html).

For example, on my Linux server the path to my player-specific Lua library is:

    /home/minecraftserver/config/wizards-of-lua/libs/0c77f1a8-943b-4f7b-aa09-f2b1737d4f03/

Or, when playing single-player on my Mac, the path is:

    ~/Library/Application Support/minecraft/config/wizards-of-lua/libs/0c77f1a8-943b-4f7b-aa09-f2b1737d4f03/


By using the <tt>require</tt> function you can include any file from the library directory.

### Importing a Player-specific Module
Let's assume that in your library directory there exists a file called "<tt>utilities.lua</tt>" with the following contents:
```lua
function particle(name)
  spell:execute("particle "..name.." ~ ~ ~ 0 0 0 0 1")
end
```

If you want to use the <tt>particle</tt> function in your current spell, you can include your utilities module by using the <tt>require</tt> function.

```lua
/lua require("utilities"); particle("smoke")
```
Please note that you must omit the module's file extension.

If you want to always import a specific module for any of your spells, you might want to add it to your player-specific profile.

### Using a Player-specific Profile
A player-specific profile is an ordinary Lua file inside the player's library directory that is configured to be autimatically required by any of your spells.
This can be done by using the [/wol command](/wol-command):
```
/wol autoRequire set <module>
```

#### Example
Let's assume there is a file called "<tt>myprofile.lua</tt>" inside your library directory with the following contents:

```lua
-- this is my profile
require "utilities"
```
To set this file as your profile you just have to configure it as follows:

```
/wol autoRequire set myprofile
```

Now you can call your <tt>particle</tt> function from your utilities module directly from any of your spells:
```lua
/lua particle("smoke")
```
