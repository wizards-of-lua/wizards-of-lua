#### Example
Creating a standing sign with the name of the current spell's owner written
onto it and placing it at the spell's position:
```lua
spell.block = Blocks.get( "standing_sign"):withNbt( {
  Text1 = '{"text":"'..spell.owner.name..'"}'
})
```

#### Example
Putting a stack of 64 wheat bundles into slot no. 5 of the chest (or the shulker box)
at the spell's position:
```lua
spell.block = spell.block:withNbt( {
  Items = {
    { Count = 64, Damage = 0, Slot = 5, 
      id = "minecraft:wheat"
    }
  }
})
```
