---
name: Items
subtitle: Creating Items
type: module
layout: module
properties:
functions:
  - name: get
    parameters: string, amount
    results: "[Item](!SITE_URL!/modules/Item/)"
    description: "The 'get' function returns an new [item](!SITE_URL!/modules/Item/) of the given type and amount.
    "
    examples:
      - url: Items/get.md
---

The <span class="notranslate">Items</span> module can be used to create an [item](/modules/Item/) of any type.
