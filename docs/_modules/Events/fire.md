#### Example
Fire a custom event with some text content:
```lua
local content = "hello!"
events.fire( "my event type name", content)
```
#### Example
Fire a custom event with some table content:
```lua
local content = { message="hello", pos=spell.pos, player=spell.owner.name}
events.fire( "my event type name", content)
```
