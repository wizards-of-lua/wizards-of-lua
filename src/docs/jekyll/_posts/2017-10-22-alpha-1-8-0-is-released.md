---
title:  "Alpha 1.8.0 is Released"
date:   2017-10-22 16:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.8.0 alpha is a pure feature release.
Most notably it brings the new [SwingArmEvent](/versions/current/modules/SwingArmEvent),
which is fired when a player swings his left or right arm.
This is extreamly useful, for example, if you want to create a magic wand.
<!--more-->
See the [Rocket Thrower example](/versions/current/examples/rocket-thrower) for more information.

* Fixed [#67](https://github.com/wizards-of-lua/wizards-of-lua/issues/67) - Support the configuration of a shared profile. See the docs for [/wol sharedAutoRequire](/versions/current/wol-command#Shared-Default-Dependencies).
* Fixed [#71](https://github.com/wizards-of-lua/wizards-of-lua/issues/71) - Support [swing-arm event](/versions/current/modules/SwingArmEvent).
* Fixed [#74](https://github.com/wizards-of-lua/wizards-of-lua/issues/74) - Wol should create the luaLibDirHome and the player-specific libDir folders automatically at startup.
* Fixed [#75](https://github.com/wizards-of-lua/wizards-of-lua/issues/75) - Allow the configuration of the the shared lib directory location. See the [configuration file](/versions/current/configuration-file) for more details.
* Fixed [#77](https://github.com/wizards-of-lua/wizards-of-lua/issues/77) - Add caching of compiled modules. This speeds up the creation of new spells!
* Fixed [#78](https://github.com/wizards-of-lua/wizards-of-lua/issues/78) - Add a [module for creating items](/versions/current/modules/Items).
* Fixed [#79](https://github.com/wizards-of-lua/wizards-of-lua/issues/79) - Support [converting a block into an item](/versions/current/modules/Block/#asItem).
