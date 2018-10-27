#### Example
Echoing all chat messages.
```lua
local queue = Events.collect("ChatEvent")
while true do
  local event = queue:next()
  spell:execute("say %s", event.message)
end
```

#### Example
Posting the position of all block-click events into the chat.
```lua
local queue=Events.collect("LeftClickBlockEvent","RightClickBlockEvent")
while true do
  local event = queue:next()
  spell:execute("say %s at %s", event.name, event.pos)
end
```
