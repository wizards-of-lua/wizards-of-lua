---
title: The Knowledge of Who is Here
name: Players
properties: 
functions:
  - name: ids
    parameters:
    results: ids
    description: "The 'ids' function returns the ids of all players
    currently logged in."
    examples:
    - url: Players/ids.md
  - name: get
    parameters: id
    results: Player
    description: "The 'get' function returns the player with the given
    ID, or nil if no such player exists."
    examples:
    - url: Players/get.md
  - name: find
    parameters: selector
    results: ids
    description: "The 'find' function returns all players who match the
    given selector."
    examples:
    - url: Players/find.md
  - name: names
    parameters:
    results: table of names
    description: "The 'names' function returns the names of all players
    currently logged in."
    examples:
    - url: Players/names.md
  - name: getByName
    parameters: name
    results: Player
    description: "The 'getByName' function returns the player with the given
    name, or nil if no such player exists."
    examples:
    - url: Players/getByName.md
---
{% include module-head.md %}

The Players module provides access to all players who are currently logged in.

{% include module-body.md %}
