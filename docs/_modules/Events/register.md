#### Example
Register for CHAT events and echo the received messages:
```lua
local queue = Events.register( "CHAT")
for e in queue.next do
  spell:say( e.message)
end
```
#### Example
Register for LEFT_CLICK and RIGHT_CLICK events and tell
the world what happend:
```lua
local queue = Events.register( "LEFT_CLICK", "RIGHT_CLICK")
for e in queue.next do
  spell:say( e.type.." at "..e.pos);
end
```
#### Example
Register for some custom event type and tell
the world what happend:
```lua
local queue = Events.register( "my event type name")
for e in queue.next do
  spell:say( inspect( e) )
end
```
