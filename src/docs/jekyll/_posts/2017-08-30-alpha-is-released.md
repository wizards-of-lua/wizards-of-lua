---
title:  "Alpha 1.1.0 is Released"
date:   2017-08-30 15:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The new 1.1.0-alpha contains just a single fix.
Now you can set the number of Lua ticks that a spell can use during a single game tick.
<!--more-->
This closes ticket [#48](https://github.com/wizards-of-lua/wizards-of-lua/issues/48) of the [roadmap](/roadmap).

You can set the value eighter by editing the config file at <tt>config/wizards-of-lua/wizards-of-lua.luacfg</tt> (located inside your Minecraft folder), or online with the [/wol command](/wol-command).

For example:
```
/wol luaTicksLimit set 100000
```
will increase the standard value by a factor of 10.
This will in fact make your spells about 10 times faster.
But use this with care, since this can slow down your Minecraft server, especially
when there are a lot of spells runnung concurrently.
