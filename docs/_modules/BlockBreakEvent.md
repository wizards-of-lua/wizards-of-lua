---
name: BlockBreakEvent
title: BlockBreakEvent
subtitle: When a Player Breaks a Block
type: event
extends: BlockEvent
layout: module
properties:
  - name: experience
    type: 'number (int)'
    access: r/w
    description: |
        This is the amount of experience to drop by the block, if the event won't be canceled.
  - name: player
    type: '[Player](/modules/Player)'
    access: r
    description: |
        This is the [player](/modules/Player) who broke the block.
functions:
---

The <span class="notranslate">BlockBreakEvent</span> is fired when an Block is about to be broken
by a player.

Canceling this event will prevent the Block from being broken.
