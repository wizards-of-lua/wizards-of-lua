#### Example
Busy-waiting for a chat event and printing the message when it occurs.
```lua
local queue=Events.connect("ChatEvent")
while queue:isEmpty() do
  sleep(20)
  print("still waiting...")
end
local event=queue:pop(0)
print("You said "..event.message)
```
