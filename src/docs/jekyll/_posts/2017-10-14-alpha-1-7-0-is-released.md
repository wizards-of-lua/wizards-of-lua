---
title:  "Alpha 1.7.0 is Released"
date:   2017-10-14 13:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.7.0-alpha introduces event handling. A bunch of in-game events and custom events are supported now!
<!--more-->

* Fixed [#38](https://github.com/wizards-of-lua/wizards-of-lua/issues/38) - Spells should be able to receive in-game events. See [Events.connect()](/versions/current/modules/Events/#connect) for more information.
* Fixed [#39](https://github.com/wizards-of-lua/wizards-of-lua/issues/39) - Spells should be able to send and receive custom events. See [Events.fire()](/versions/current/modules/Events/#fire) for more information.

Supported events are:
* [ChatEvent](/versions/current/modules/ChatEvent/)
* [RightClickBlockEvent](/versions/current/modules/RightClickBlockEvent/)
* [LeftClickBlockEvent](/versions/current/modules/LeftClickBlockEvent/)
* [CustomEvent](/versions/current/modules/CustomEvent/)
