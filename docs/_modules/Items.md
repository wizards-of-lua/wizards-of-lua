---
name: Items
title: Items
subtitle: Creating Items
type: module
layout: module
properties:
functions:
  - name: 'get'
    parameters: name, amount
    results: '[Item](/modules/Item)'
    description: |
        The 'get' function returns a new [item](/modules/Item/) of the given type and amount.
       
        #### Example
       
        Creating one diamond axe and putting it into the player's hand.
       
        ```lua
        local axe = Items.get("diamond_axe")
        spell.owner.mainhand = axe
        ```
       
        #### Example
       
        Creating a full stack of wheat and putting it into the wizard's hand.
       
        ```lua
        spell.owner.mainhand = Items.get("wheat", 64)
        ```
---

The <span class="notranslate">Items</span> module can be used to create an [item](/modules/Item/)
of any type.
