#### Example
Echo the last chat message every 5 seconds.
```lua
local queue = Events.connect("ChatEvent")
while true do
  local event = queue:latest()
  spell:execute("say %s", event.message)
  sleep(100)
end
```
