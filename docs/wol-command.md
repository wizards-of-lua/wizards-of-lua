---
title: The Wol Command
---
The <tt>/wol</tt> command (wol is short for Wizards Of Lua) gives you some
control over the Winzards of Lua mod.

As with the alpha version this command is still very limited.
Here are all available actions:

### Listing all active spells
To get a list of all active spells just type:
```
/wol spell list all
```

### Breaking all active spells
To break all active spells just type:
```
/wol spell break all
```

### Setting the Lua ticks limit
To assign a new value to the Lua ticks limit use <tt>/wol luaTicksLimit</tt>

For example:
```
/wol luaTicksLimit set 100000
```
will set the number of Lua tick a spell can use during a single game tick to 100000.
Allowed are values between 1000 and 10000000.
Default is 10000.

The new value will also stored into the config file at <tt>config/wizards-of-lua/wizards-of-lua.cfg</tt>, which means that it survives a server restart.

Please use this with care, since this can slow down your Minecraft server, especially
when there are a lot of spells runnung concurrently.

### Showing the Lua ticks limit
To see the current value of the Lua ticks limit use <tt>/wol luaTicksLinit</tt>

For example:
```
/wol luaTicksLimit
```
will print the value into you chat screen.
