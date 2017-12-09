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

<a name="Shared-Default-Dependencies" style="position:relative; top:-70px; display:block;"></a>
## Shared Default Dependencies
The <tt>sharedAutoRequire</tt> value is a global value that defines which Lua module will be loaded
automatically into a new spell's environment.
The respective module must be available within the [shared library](/configuration-file.html) (or all
of the player's libraries).

### Showing the sharedAutoRequire value
```
/wol sharedAutoRequire
```
This prints the value of the "sharedAutoRequire" setting.

### Setting the sharedAutoRequire value
Lets assume that there is a file called "shared-profile.lua" inside the
sharred library directory.
```
/wol sharedAutoRequire set shared-profile
```
This sets the "shared-profile" module as the automatic requirement for all spells.
This is equivalent to prefixing a new spell's code with <tt>require "shared-profile"</tt>.

### Unsetting the sharedAutoRequire value
```
/wol sharedAutoRequire unset
```
This sets the value of the "sharedAutoRequire" setting to none.

<br/>

<a name="Default-Dependencies" style="position:relative; top:-70px; display:block;"></a>
## Player-specific Default Dependencies
The <tt>autoRequire</tt> value is a player-specific value that defines which Lua module will be loaded
automatically into a new spell's environment.
The respective module must be available within the [player's Lua path](/configuration-file.html).

### Showing the autoRequire value
```
/wol autoRequire
```
This prints the value of the "autoRequire" setting.

### Setting the autoRequire value
Lets assume that the current player has a file called "myprofile.lua" in his
Lua library directory.
```
/wol autoRequire set myprofile
```
This sets the "myprofile" module as the automatic requirement for the current player.
This is equivalent to prefixing a new spell's code with <tt>require "myprofile"</tt>.

### Unsetting the autoRequire value
```
/wol autoRequire unset
```
This sets the value of the "autoRequire" setting to none.

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
