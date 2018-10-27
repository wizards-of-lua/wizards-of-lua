#### Example
Adding the wizard to the 'rogues' team.
```lua
spell.owner.team = "rogues"
```

To make this work, don't forget to create the 'rogues' team first:
```
/scoreboard teams add rogues
```

#### Example
Printing the wizard's team name.
```lua
print( spell.owner.team)
```
