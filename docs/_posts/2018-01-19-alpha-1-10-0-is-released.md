---
title:  "Alpha 1.10.0 is Released"
date:   2018-01-19 12:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.10.0 alpha is a huge feature release. Please note that the new version is not completely backwards compatible.
<!--more-->

Most notably is the new web interface for [editing Lua files](/wol-command.html#Personal-Files) directly from within your web browser.

![Random Walking Grass](/images/wol-file-editor.jpg)

Backwards compatibility is broken because of [issue #95](https://github.com/wizards-of-lua/wizards-of-lua/issues/95).
If you have used this feature, then you can safely migrate to the new version by:
* renaming your personal auto-required file to "profile.lua".
* renaming your shared auto-required file to "shared-profile.lua".

These names are now hard-coded.

Here is a list of all changes in release 1.10.0-alpha:

* Fixed [#91](https://github.com/wizards-of-lua/wizards-of-lua/issues/91) - Support startup module that is executed on server startup.
* Fixed [#92](https://github.com/wizards-of-lua/wizards-of-lua/issues/92) - Create web interface to support editing of Lua files with the web browser.
* Fixed [#93](https://github.com/wizards-of-lua/wizards-of-lua/issues/93) - Add a link to our public Discord channel into the Lua editor GUI.
* Fixed [#94](https://github.com/wizards-of-lua/wizards-of-lua/issues/94) - Add a link to our online documentation to the Lua editor.
* Fixed [#95](https://github.com/wizards-of-lua/wizards-of-lua/issues/95) - Remove "autorequire" and instead search for profiles with fixed names.
* Fixed [#96](https://github.com/wizards-of-lua/wizards-of-lua/issues/96) - Make Entity.lookVec writable.
* Fixed [#97](https://github.com/wizards-of-lua/wizards-of-lua/issues/97) - Add Entity.scanView().
* Fixed [#98](https://github.com/wizards-of-lua/wizards-of-lua/issues/98) - Add Entity:dropItem().
* Fixed [#99](https://github.com/wizards-of-lua/wizards-of-lua/issues/99) - Add an optional parameter "amount" to Items.get().
* Fixed [#100](https://github.com/wizards-of-lua/wizards-of-lua/issues/100) - Add an optional parameter "amount" to Block:asItem().
* Fixed [#101](https://github.com/wizards-of-lua/wizards-of-lua/issues/101) - Player.rotationYaw returns incorrect value.
* Fixed [#102](https://github.com/wizards-of-lua/wizards-of-lua/issues/102) - Spell:execute should only format its content when arguments are given.
* Fixed [#103](https://github.com/wizards-of-lua/wizards-of-lua/issues/103) - Add an "Object" superclass for all WoL classes.
* Fixed [#104](https://github.com/wizards-of-lua/wizards-of-lua/issues/104) - Add Vec3:normalize().
