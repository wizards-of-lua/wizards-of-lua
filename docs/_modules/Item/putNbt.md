#### Example
Setting the lore (description) of the item in the wizard's hand.
```lua
local text1 = "Very important magical stuff."
local text2 = "Use with caution!"
local loreTbl = { text1, text2}
local item = spell.owner.mainhand
item:putNbt({tag={display={Lore=loreTbl}}})
```
