#### Example
Creating a standing sign with the health of the current spell's owner written
onto it.
```lua
spell.block = "standing_sign"
local p = spell.owner
Blocks.putData(spell.pos, {
  Text1 = '{"text":"'..p.name..'"}',
  Text2 = '{"text":"Health"}',
  Text3 = '{"text":"'..p.health..'"}',
})
```
