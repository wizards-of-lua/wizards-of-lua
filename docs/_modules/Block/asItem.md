#### Example
Creating an item from the block at the spell's current position and putting
it into the wizard's offhand.
```lua
item=spell.block:asItem(); spell.owner.offhand=item
```

Creating a full stack of of the block at the spell's current position and putting
it into the wizard's offhand.
```lua
item=spell.block:asItem(64); spell.owner.offhand=item
```
