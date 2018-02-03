#### Example
Transform all torches that are placed against a redstone block into redstone torches.
```lua
local queue = Events.connect("BlockPlaceEvent")
while true do
  local event = queue:next()
  if event.block.name == 'torch' and event.placedAgainst.name == 'redstone_block' then
    spell.pos = event.pos
    spell.block = Blocks.get('redstone_torch'):withData(event.block.data)
  end
end
```
