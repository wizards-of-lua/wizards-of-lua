#### Example
Echoing all chat messages.
```lua
local queue = Events.connect("ChatEvent")
while true do
  local event = queue:next()
  spell:execute("say %s", event.message)
end
```
