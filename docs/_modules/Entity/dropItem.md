#### Example
Dropping the block at the spell's position as an item.

```lua
if spell.block.name ~= "air" then
  item = spell.block:asItem()
  spell:dropItem( item)
  spell.block = Blocks.get("air")
end
```
