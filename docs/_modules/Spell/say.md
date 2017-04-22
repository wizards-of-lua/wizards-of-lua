#### Example
Saying 'hello!' to everyone:
```lua
spell:say("hello!")
```
#### Example
Telling all players every second what time it is.
```lua
while true do
  spell:say( Runtime.getRealtime(), Runtime.getGametime(), Runtime.getLuatime())
  sleep( 20)
end
```
