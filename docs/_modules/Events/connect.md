#### Example
Echoing all chat messages.
```lua
local queue=Events.connect("ChatEvent")
while true do
  local event=queue:pop()
  spell:execute("say %s", event.message)
end
```

#### Example
Posting the position of all block-click events into the chat.
```lua
local queue=Events.connect("LeftClickBlockEvent","RightClickBlockEvent")
while true do
  local event=queue:pop()
  spell:execute("say %s at %s", event.name, event.pos)
end
```
