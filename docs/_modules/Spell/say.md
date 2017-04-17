#### Example
Say 'hello!' to everyone:
```lua
spell:say("hello!")
```
#### Example
Tell all players every second what time it is.
```lua
while true do
  spell:say( runtime.getRealtime(), runtime.getGametime(), runtime.getLuatime())
  sleep( 20)
end
```
