---
title: The Wol Command
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

For example, to import the [Bomberman Gist](https://gist.github.com/mkarneim/a62a55f14ed076ec08bccc0302d7f41c), which has the Gist ID <tt>a62a55f14ed076ec08bccc0302d7f41c</tt>, type the following:

```
/wol file gist get a62a55f14ed076ec08bccc0302d7f41c
```
This will download the file 'bomberman.lua' from the Bomberman Gist into your personal directory.

Alternatively you also could use the Gist's URL instead of its ID:

```
/wol file gist get https://gist.github.com/mkarneim/a62a55f14ed076ec08bccc0302d7f41c
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

For example, to import the [Bomberman Gist](https://gist.github.com/mkarneim/a62a55f14ed076ec08bccc0302d7f41c), which has the Gist ID <tt>a62a55f14ed076ec08bccc0302d7f41c</tt>, type the following:

```
/wol shared-file gist get a62a55f14ed076ec08bccc0302d7f41c
```
This will download the file 'bomberman.lua' from the Bomberman Gist into the shared directory.

Alternatively you also could use the Gist's URL instead of its ID:

```
/wol shared-file gist get https://gist.github.com/mkarneim/a62a55f14ed076ec08bccc0302d7f41c
```

<br/>


<a name="Lua-Ticks-Limit" style="position:relative; top:-70px; display:block;"></a>
## Lua Ticks Limit Configuration
The <tt>luaTicksLimit</tt> value defines how many Lua ticks each spell can use during
a single game tick. When this value is exceeded, the spell will be broken or
sent to sleep for one game tick. This depends on the [Time.autosleep](/modules/Time/#autosleep) setting.

### Showing the Lua Ticks Limit
```
/wol luaTicksLimit
```
This will print the current value into your chat screen.

### Setting the Lua Ticks Limit
```
/wol luaTicksLimit set 100000
```
This will set the number of Lua tick a spell can use during a single game tick to 100000.
Allowed are values between 1000 and 10000000.
Default is 10000.

The new value will also stored into the config file at <tt>config/wizards-of-lua/wizards-of-lua.luacfg</tt>, which means that it survives a server restart.

Please use this with care, since this can slow down your Minecraft server, especially
when there are a lot of spells runnung concurrently.
