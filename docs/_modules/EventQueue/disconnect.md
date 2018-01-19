#### Example
Connecting an event queue to the chat event source and disconnecting it after
the first event occurs.
```lua
local queue = Events.connect("ChatEvent")
local event = queue:next()
print(str(event))
queue:disconnect()
```
