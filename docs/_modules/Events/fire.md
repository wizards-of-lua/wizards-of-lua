#### Example
Fire a custom event with some text content:
```lua
local content = "hello!"
Events.fire( "my event type name", content)
```
#### Example
Fire a custom event with some table content:
```lua
local content = { message="hello", pos=spell.pos, player=spell.owner.name}
Events.fire( "my event type name", content)
```
