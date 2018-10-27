#### Example
Creating a smooth diorite block and placing it at the spell's position.
```lua
spell.block = Blocks.get( "stone"):withData(
  { variant = "smooth_diorite"}
)
```

#### Example
Creating a bundle of full grown wheat on top of the block at the spell's position.
```lua
spell:move( "up")
spell.block = Blocks.get( "wheat"):withData(
  { age = 7}
)
```
