---
name: RightClickItemEvent
title: RightClickItemEvent
subtitle: When a Player Right-Clicks with an Item
type: event
extends: PlayerInteractEvent
layout: module
properties:
functions:
---

The <span class="notranslate">RightClickItemEvent</span> class is fired when a player
right-clicks somewhere with an [Item](/modules/Item).

Note that this is NOT fired if the player is targeting a block (see
[RightClickBlockEvent](/modules/RightClickBlockEvent)) or an entity (see
[PlayerEntityInteractEvent](/modules/PlayerEntityInteractEvent)).
