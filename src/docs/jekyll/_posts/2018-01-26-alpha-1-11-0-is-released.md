---
title:  "Alpha 1.11.0 is Released"
date:   2018-01-26 10:00:00
categories: release
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
The 1.11.0 alpha is a small API refactoring release. Please note that this version is not completely backwards compatible.
<!--more-->

* Fixed [#107](https://github.com/wizards-of-lua/wizards-of-lua/issues/107) - Rename EventQueue:pop to EventQueue:next.
* Fixed [#108](https://github.com/wizards-of-lua/wizards-of-lua/issues/108) - New function EventQueue:latest.
* Fixed [#109](https://github.com/wizards-of-lua/wizards-of-lua/issues/109) - Rename Entity.orientation to Entity.facing.

### Migration Manual
To migrate your spells to this version, please do the following steps:

1) Find all occurrences of EventQueue:pop() and replace them with EventQueue:next()

For example:
```lua
local queue = Events.connect("ChatEvent")
while true do
  local event = queue:pop()
  ...
```
Should become:
```lua
local queue = Events.connect("ChatEvent")
while true do
  local event = queue:next()
  ...
```

2) Find all occurrences of Entity.orientation and replace them with Entity.facing

For example:
```lua
if spell.orientation == "north" then
  ...
```
Should become:
```lua
if spell.facing == "north" then
  ...
```
