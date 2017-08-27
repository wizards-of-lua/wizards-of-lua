---
title:  "Alpha 1.0.0 is Released!"
date:   2017-08-26 10:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
Today we proudly present the very first alpha release of "Wizards of Lua".
If you are interested into this mod at this early stage, please feel free to
[download and install](/installation/) it.
<!--more-->
Make sure to read the [introduction](/introduction/) in order to get some hints
about how to use this mod.
We are eager to receive some [feedback about issues](https://github.com/wizards-of-lua/wizards-of-lua/issues)
you discover.

The following features are implemented so far.
### Implemented Features
* Wizards can cast spells using the [/lua command](/lua-command).
* Wizards can list all active spells using the [/wol command](/wol-command).
* Wizards can break all active spells using the [/wol command](/wol-command).
* Spells can be casted by command blocks.
* Spells can create any available block using the [Blocks module](/modules/Blocks/).
* Spells can read and modify all block properties using the [Block class](/modules/Block/).
* Spells can copy and paste blocks using the [Spell.block property](/modules/Spell/#block).
* Spells can read and modify a small amount of entity properties using the [Entity class](/modules/Entity/).
* Spells (in fact all entities) can be [moved](/modules/Entity/#move ) around.
* Spells can be made [visible](/modules/Spell/#visible).
* Spells can [execute](/modules/Spell/#execute) any Minecraft command. This includes casting other spells.
* Spells can be sent to [sleep](/modules/Runtime/#sleep) for a certain amount of game ticks.

However, this version is marked as *Alpha*, which means that there are still
important features missing.


The following features are planned to be implemented before we make the final release.
### Planned Features
* Spells can read and write more entity properties.
* Spells can read and write entity NBT data.
* Spells can query for entities.
* Spells can receive in-game events, e.g. Left-Click, Right-Click, Player-Logged-In, ...
* Spells can send and receive custom events.
* Spells can import Lua files from the server's file system using Lua's `require` function.
* Spells can import Lua files from GitHub Gists.
* Spells can share data using a player-specific clipboard.
* Wizards can define a player-specific profile to configure default includes.
* Wizards can list their own active spells.
* Wizards can break specific spells.
* Wizards can define event handles that cast spells automatically.
* Operators can define permissions requires for casting a spell.
* Operators can configure the maximum number of Lua ticks a spell can use per game tick.
