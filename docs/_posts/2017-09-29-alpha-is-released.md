---
title:  "Alpha 1.5.0 is Released"
date:   2017-09-29 15:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.5.0-alpha is available. It brings a ton of new entity properties and adds
support for relative movement to Entity.move().
<!--more-->

* Fixed [#35](https://github.com/wizards-of-lua/wizards-of-lua/issues/35) - Spells should read and write more entity properties: [rotationYaw](/modules/Entity/#rotationYaw), [rotationPitch](/modules/Entity/#rotationPitch), [eyeHeight](/modules/Entity/#eyeHeight), [lookVec](/modules/Entity/#lookVec), [tags](/modules/Entity/#tags), [orientation](/modules/Entity/#orientation),  [motion](/modules/Entity/#motion). We also have a new Player property: [team](/modules/Player/#team).
* Fixed [#58](https://github.com/wizards-of-lua/wizards-of-lua/issues/58) - Entity.putNbt() should support setting tags
* Fixed [#61](https://github.com/wizards-of-lua/wizards-of-lua/issues/61) - Rename "Runtime" module to ["Time"](/modules/Time) and replace most function with properties
* Fixed [#62](https://github.com/wizards-of-lua/wizards-of-lua/issues/62) - A new spell's orientation should be the same as the wizard's orientation
* Fixed [#63](https://github.com/wizards-of-lua/wizards-of-lua/issues/63) - [Entity.move()](/modules/Entity/#move) should accept relative directions (forward, back, left, right)
* Fixed [#64](https://github.com/wizards-of-lua/wizards-of-lua/issues/64) - Replacing a block with modified nbt not working

Please note that this version is not backwards compatible since the former Runtime module has been renamed to Time.
