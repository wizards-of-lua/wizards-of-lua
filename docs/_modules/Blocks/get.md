#### Example
Creating a stone block and placing it at the spell's position:
```lua
spell.block = Blocks.get( "stone")
```

#### Example
Creating a smooth diorite block and placing it at the spell's position:
```lua
spell.block = Blocks.get( "stone"):withData( { variant = "smooth_diorite"})
```

#### Example
Creating a standing sign with the name of the current spell's owner written
onto it and placing it at the spell's position:
```lua
spell.block = Blocks.get("standing_sign"):withNbt( {
  Text1='{"text":"'..spell.owner.name..'"}'
})
```
