#### Example
Collecting chat events and stopping the collection right after
the first event occurred.
```lua
local queue = Events.collect("ChatEvent")
local event = queue:next()
print(str(event))
queue:stop()
```
