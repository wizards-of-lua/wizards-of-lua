---
name: Player
subtitle: Controlling the Player
layout: module
extends: Entity
properties:
  - name: team
    type: string
    access: r/w
    description: "The 'team' is the name of the team this player belongs to, or nil if he is not a member of any team.
    "
    examples:
      - url: Player/team.md
functions:
---

The Player class represents a specific player who is currently logged into your world.
