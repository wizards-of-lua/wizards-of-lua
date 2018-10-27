#### Example
Firing a custom event without any data.
```lua
Events.fire("my-event")
```

#### Example
Firing a custom event with some primitive data.
```lua
local data = 42
Events.fire("my-event", data)
```

#### Example
Firing a custom event with some complex data.
```lua
local data = {pos=spell.pos, time=Time.gametime}
Events.fire("my-event", data)
```
