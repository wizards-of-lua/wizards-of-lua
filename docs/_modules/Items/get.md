#### Example
Creating one diamond axe and putting it into the wizard's hand.
```lua
axe=Items.get("diamond_axe"); spell.owner.mainhand=axe
```

#### Example
Creating a full stack of wheat and putting it into the wizard's hand.
```lua
spell.owner.mainhand=Items.get("wheat", 64)
```
