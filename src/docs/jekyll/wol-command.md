---
title: The Wol Command
layout: default
---
*The <tt>/wol</tt> command (wol is short for Wizards Of Lua) gives you some
control over the Winzards of Lua mod.*

<br/>

## Listing Spells
### Listing all Active Spells

```
/wol spell list all
```
This will print the list of all active spells.
For each spell you will get the spell ID (SID), followed by the first 40 characters of its code.

### Listing all Active Spells by Owner

```
/wol spell list byOwner mickkay
```
This will print the list of all active spells owned by player mickkay.

A shortcut to this is
```
/wol spell list
```

### Listing all Active Spells by Name

```
/wol spell list byName funny-name
```
This will print the list of all active spells called 'funny-name'.

### Listing all Active Spells by Name

```
/wol spell list bySid 1234
```
This will print the list of all active spells with spell ID 1234.
Since spell IDs are unique, this will give you 1 match at most.


<br/>

## Breaking Spells
There are several options to break spells.

### Breaking all Active Spells

```
/wol spell break all
```
This will break all active spells immediately.

### Breaking Spells by Name

```
/wol spell break byName Spell-1
```
This will break all spells with name "Spell-1".
Please note that you can [change the name](/modules/Spell/#name) of any spell.

### Breaking Spells by Spell ID

```
/wol spell break bySid 15
```
This will break the spell with the spell id 15.

### Breaking Spells by Owner

```
/wol spell break byOwner mickkay
```
This will break all spells owned by player mickkay.

<br/>


<a name="Browser" style="position:relative; top:-70px; display:block;"></a>
## Browser
### Login
If you want to edit your Lua files with your web browser, your have to log in first.

*Example:*
```
/wol browser login
```
This will print a web link into the chat.
Just press T and click on it.
Minecraft will open your web browser and load an authentication token.
After that you can edit your files with your browser.

### Logout
If you don't want to edit your Lua files with your web browser anymore, your can log out.

*Example:*
```
/wol browser logout
```
This will generate a new authentication token for your.
After that you can not edit your files with your browser anymore, unless of course, you log in again.

<br/>


<a name="Personal-Files" style="position:relative; top:-70px; display:block;"></a>
## Personal Files
In order to store your spells for later use, you can create a file and save it into
your personal directory.
Then you can load your spells any time after by calling ```require```.

*Example:*

To load the contents of the file ```myfile.lua``` into the current spell and
call the function ```myFunc()``` which is declared in that file, just execute the following
command:

```lua
/lua require("myfile"); myFunc()
```

Per default, a personal file is only visible to the owner.
In order to access it from outside, e.g. from within a command block, you have to call ```addpath```.

*Example:*

```lua
/lua addpath("mickkay"); require("myfile"); myFunc()
```
This spell adds mickkay's personal directory to the spell's Lua search path.
After that, the spell can load any of mickkay's files.

<a name="File-Edit" style="position:relative; top:-70px; display:block;"></a>
### Creating a New File
To create a new file in your personal directory, just submit the following command:
```
/wol file edit <newfilename>
```
where *&lt;newfilename&gt;* will be the name of the new file.
This will print the web link of the new file into the chat.
Just press T and then click on it.
Minecraft will open a Lua editor for your new file in your web browser.

For example, to create a file with the name "profile.lua", just type:
```
/wol file edit profile.lua
```

### Editing an Existing File
To edit an existing file from your personal directory, submit the following command:
```
/wol file edit <filename>
```
where *&lt;filename&gt;* is the name of the file.
This will print the web link of the file into the chat.
Just press T and then click on it.
Minecraft will open a Lua editor for your file in your web browser.

For example, to edit a file with the name "magic/rocket.lua", just type:
```
/wol file edit magic/rocket.lua
```

### Moving an Existing File
To move an existing file from your personal directory to some new location, submit the following command:
```
/wol file move <filename> <newfilename>
```
where *&lt;filename&gt;* is the old name of the file and
*&lt;newfilename&gt;* is the new name of the file.

For example, to move a file with the name "magic/rocket.lua" to "items/rocket.lua", just type:
```
/wol file move magic/rocket.lua items/rocket.lua
```

### Deleting an Existing File
To delete an existing file from your personal directory, submit the following command:
```
/wol file delete <filename>
```
where *&lt;filename&gt;* is the name of the file.

For example, to delete a file with the name "magic/rocket.lua", just type:
```
/wol file delete magic/rocket.lua
```

<a name="Import-Gist" style="position:relative; top:-70px; display:block;"></a>
### Importing Files from GitHub Gists
To import a GitHub Gist into your personal directory, submit the following command:
```
/wol file gist get <gist-id> <target-folder>
```
where *&lt;gist-id&gt;* is the ID of the Gist you want to import,
and *&lt;target-folder&gt;* is the name of the folder, where the Gist's contents should be stored.

The specification of the target folder is optional. If not specified, the top-level folder is used as target folder.

Please note, that this is a limited function.
As of a restriction by GitHub, your Minecraft server can only download a maximum number of 50 Gists per hour.
However, if you want to increase this limit, please consider [configuring a personal GitHub access token](/configuration-file#General).

For example, to import the [Tetris Gist](https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3), which has the Gist ID <tt>a86a1e7a6c02fbd850d2ef4d4b618fb3</tt>, type the following:

```
/wol file gist get a86a1e7a6c02fbd850d2ef4d4b618fb3
```
This will download the file 'tetris.lua' from the Tetris Gist into your personal directory.

Alternatively you also could use the Gist's URL instead of its ID:

```
/wol file gist get https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3
```

<br/>

<a name="Shared-Files" style="position:relative; top:-70px; display:block;"></a>
## Shared Files
Files inside the shared directory are called shared files.
They are visible to all wizards and have no specific owner.
Anybody with operator privileges can use, view, and edit these files.

### Creating a New Shared File
To create a new shared file, just submit the following command:
```
/wol shared-file edit <newfilename>
```
where *&lt;newfilename&gt;* will be the name of the new file.
This will print the web link of the new file into the chat.
Just press T and then click on it.
Minecraft will open a Lua editor for your new file in your web browser.

For example, to create a shared file with the name "shared-profile.lua", just type:
```
/wol shared-file edit shared-profile.lua
```

### Editing an Existing Shared File
To edit an existing shared file, submit the following command:
```
/wol shared-file edit <filename>
```
where *&lt;filename&gt;* is the name of the file.
This will print the web link of the new file into the chat.
Just press T and then click on it.
Minecraft will open a Lua editor for your new file in your web browser.

For example, to edit a shared file with the name "magic/rocket.lua", just type:
```
/wol shared-file edit magic/rocket.lua
```

### Moving an Existing Shared File
To move an existing shared file to some new location, submit the following command:
```
/wol shared-file move <filename> <newfilename>
```
where *&lt;filename&gt;* is the old name of the file and
*&lt;newfilename&gt;* is the new name of the file.

For example, to move a file with the name "magic/rocket.lua" to "items/rocket.lua", just type:
```
/wol shared-file move magic/rocket.lua items/rocket.lua
```

### Deleting an Existing Shared File
To delete an existing shared file, just submit the following command:
```
/wol shared-file delete <filename>
```
where *&lt;filename&gt;* is the name of the file.

For example, to delete a file with the name "magic/rocket.lua", just type:
```
/wol shared-file delete magic/rocket.lua
```

### Importing Files from GitHub Gists into the Shared Directory
To import a GitHub Gist into the shared directory, submit the following command:
```
/wol shared-file gist get <gist-id> <target-folder>
```
where *&lt;gist-id&gt;* is the ID of the Gist you want to import,
and *&lt;target-folder&gt;* is the name of the folder, where the Gist's contents should be stored.

The specification of the target folder is optional. If not specified, the top-level folder is used as target folder.

Please note, that this is a limited function.
As of a restriction by GitHub, your Minecraft server can only download a maximum number of 50 Gists per hour.
However, if you want to increase this limit, please consider [configuring a personal GitHub access token](/configuration-file#General).

For example, to import the [Tetris Gist](https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3), which has the Gist ID <tt>a86a1e7a6c02fbd850d2ef4d4b618fb3</tt>, type the following:

```
/wol shared-file gist get a86a1e7a6c02fbd850d2ef4d4b618fb3
```
This will download the file 'tetris.lua' from the Tetris Gist into the shared directory.

Alternatively you also could use the Gist's URL instead of its ID:

```
/wol shared-file gist get https://gist.github.com/mkarneim/a86a1e7a6c02fbd850d2ef4d4b618fb3
```

<br/>


<a name="Pack" style="position:relative; top:-70px; display:block;"></a>
## Pack
### Export
This command is meant to be used by users who want to create a [spell pack](/spellpacks.html) with all Lua files of a given subdirectory of the libs/shared folder so that it can be distributed to other servers.
This command stores a spell pack using the contents of the given source directory (that must exist inside the libs/shared folder) into a JAR file and produces a http link to download it.

*Example:*
```
/wol pack export dummy
```
This command compresses the contents of the "dummy" directory (that must exist inside the libs/shared folder) into a JAR file and produces a http link to download it.

<br/>


<a name="Startup-Sequence" style="position:relative; top:-70px; display:block;"></a>
## Initiating the Startup Sequence
This command manually initiates the startup sequence. This is the same sequence that is automatically executed
when the server is started.

It searches all add-ons and the shared directory for startup modules (these are Lua files called "startup.lua")
and launches them. The locations are searched breadth first, and in this order the startup modules are launched.
This ensures that startup modules in a lower directory are launched only after all startup modules in all upper directory have been launched. However, the launch order of modules with the same directory level is unspecified.

```
/wol startup
```
<br/>


<a name="Lua-Ticks-Limit" style="position:relative; top:-70px; display:block;"></a>
## Lua Ticks Limit Configuration
The <tt>luaTicksLimit</tt> value defines how many Lua ticks each spell can use during
a single game tick. When this value is exceeded, the spell will be broken or
sent to sleep for one game tick. This depends on the [Time.autosleep](/modules/Time/#autosleep) setting.
For event listeners use <tt>eventListenerLuaTicksLimit</tt>.
This value defines how many Lua ticks an event handler can use for each event.

### Showing the Lua Ticks Limit
```
/wol luaTicksLimit
```
and
```
/wol eventListenerLuaTicksLimit
```
This will print the current values into your chat screen.

### Setting the Lua Ticks Limit
```
/wol luaTicksLimit set 100000
```
and
```
/wol eventListenerLuaTicksLimit set 100000
```
Allowed are values between 1000 and 10000000.
Default is 50000.

The new value will also stored into the config file at <tt>config/wizards-of-lua/wizards-of-lua.luacfg</tt>, which means that it survives a server restart.

Please use this with care, since this can slow down your Minecraft server, especially
when there are a lot of spells runnung concurrently.
