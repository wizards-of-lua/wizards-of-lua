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

To support this, the Wizards of Lua mod provides for each player a personal directory in the server's file system, that is automatically added to the player's Lua search path.

To edit files in you personal directory you could use any external text editor, provided that you have direct access to the server's file system.

But it's more convenient to use the built-in Lua editor, which can be lauched with the [/wol file edit](/wol-command.html#File-Edit) command.

### Creating a Lua Module with the Built-In Lua Editor
Let's assume you want to create a new Lua module called "utilities" and store it into your personal directory.

To do that, you first have to make sure that your web browser is allowed to access your files.
Type in the following command:
```
/wol browser login
```
and then click on the new link that appears in the chat (you must press 'T' before you can click on the link).

This will open your web browser and grant access to your personal files.

Next, create the module with the built-in editor by typing:
```
/wol file edit utilities.lua
```
and again click on the new link.

This will open a new browser tab with the Lua editor for your new file.

Now copy and paste the source code shown below into the editor and save it using CRTL-S.

```lua
function particle(name)
  spell:execute("particle "..name.." ~ ~ ~ 0 0 0 0 1")
end
```

![Built-In Editor](/images/wol-file-editor-with-utilities-module.jpg)

### Importing a Personal Module
Since your personal directory is automatically added to the Lua search path, you can import any file by using <tt>require</tt>
function.

For example, if you want to use the <tt>particle</tt> function from the "utilities" module in your current spell, you can include the module like this:

```lua
/lua require("utilities"); particle("smoke")
```
Please note that you must omit the module's file extension ".lua".

If you want to always import a specific module for any of your spells, you might want to add it to your personal profile.

### Using a Personal Profile
A personal profile is an special Lua file with the name "profile.lua" inside your directory.
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
