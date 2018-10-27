---
title:  "Alpha 1.2.1 is Released"
date:   2017-09-08 8:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The new 1.2.1-alpha release contains bug fixes.
<!--more-->

* Fixed [#50](https://github.com/wizards-of-lua/wizards-of-lua/issues/50) - Placing a door using spell.block is not working
* Fixed [#51](https://github.com/wizards-of-lua/wizards-of-lua/issues/51) - luaTicksLimit is not stored correctly into config file when changed with /wol command
* Fixed [#52](https://github.com/wizards-of-lua/wizards-of-lua/issues/52) - Add automatic version checking

Especially fix [#50](https://github.com/wizards-of-lua/wizards-of-lua/issues/50) is important since
it improves the performance when a lot of blocks are changed in a short period of time.
