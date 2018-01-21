#### Example
Echo the last chat message every 5 seconds.
```lua
local queue = Events.connect("ChatEvent")
while true do
  local event = queue:latest()
  if event ~= nil then
    spell:execute("say %s", event.message)
  end
  sleep(5 * 20)
end
```
