#### Example
Creating an item from the block at the spell's current position and putting
it into the wizard's offhand.
```lua
item=spell.block:asItem(); spell.owner.offhand=item
```
