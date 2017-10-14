---
name: Player
subtitle: Controlling the Player
type: class
extends: Entity
layout: module
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
