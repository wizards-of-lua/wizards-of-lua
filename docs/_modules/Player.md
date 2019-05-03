---
name: Player
title: Player
subtitle: Controlling the Player
type: class
extends: Entity
layout: module
properties:
  - name: 'gamemode'
    type: 'string'
    access: r/w
    description: |
        This is this player's game mode. It can be one of 'survival', 'adventure', 'creative',
        'spectator'.
       
        #### Example
       
        Setting the gamemode of this spell's owner to 'adventure'.
       
        ```lua
        spell.owner.gamemode = "adventure"
        ```
  - name: 'health'
    type: 'number (float)'
    access: r/w
    description: |
        The 'health' is the energy of this entity. When it falls to zero this entity dies.
  - name: 'mainhand'
    type: '[Item](/modules/Item)'
    access: r/w
    description: |
        This is the [item](/modules/Item) this entity is holding in its main hand.
  - name: 'offhand'
    type: '[Item](/modules/Item)'
    access: r/w
    description: |
        This is the [item](/modules/Item) this entity is holding in his off hand.
  - name: 'operator'
    type: 'boolean'
    access: r
    description: |
        This is <span class="notranslate">true</span> if this player has operator privileges.
  - name: 'team'
    type: 'string'
    access: r/w
    description: |
        The 'team' is the name of the team this player belongs to, or
        <span class="notranslate">nil</span> if he is not a member of any team.
       
        #### Example
       
        Adding the wizard to the 'rogues' team.
       
        ```lua
        spell.owner.team = "rogues"
        ```
       
        To make this work, don't forget to create the 'rogues' team first:
       
        ```lua
        /scoreboard teams add rogues
        ```
       
        #### Example
       
        Printing the wizard's team name.
       
        ```lua
        print( spell.owner.team)
        ```
functions:
---

An instance of the <span class="notranslate">Player</span> class represents a specific player who
is currently logged into the world.
