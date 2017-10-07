---
title:  "Alpha 1.6.1 is Released"
date:   2017-10-07 7:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.6.1-alpha is available. It brings a great productivity boost since Lua programs can now be loaded from the server's file system!
<!--more-->

* Fixed [#40](https://github.com/wizards-of-lua/wizards-of-lua/issues/40) - Spells should support importing Lua files from the server’s file system. See the [configuration file](/configuration-file) for more information.
* Fixed [#43](https://github.com/wizards-of-lua/wizards-of-lua/issues/43) - Wizards should be able to define a player-specific profile. See [/wol autoRequire](/wol-command.html#Automatic-Requirements) for more information.
* Fixed [#65](https://github.com/wizards-of-lua/wizards-of-lua/issues/65) - Support auto completion for [/wol spell break](/wol-command)

Please note that this version is not backwards compatible since the config file format has been changed from the standard Forge format to the Lua standard.
