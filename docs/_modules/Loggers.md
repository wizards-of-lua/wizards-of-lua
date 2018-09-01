---
name: Loggers
subtitle: Accessing Loggers
type: module
layout: module
properties:
functions:
  - name: get
    parameters: name
    results: "[Logger](/modules/Logger/)"
    description: |
      The <span class="notranslate">'get'</span> function returns the [Logger](/module/Logger) with
      the given name.

      #### Example

      Accessing the [Logger](/module/Logger) with the name "my-logger".

      ```lua
      local logger = Loggers.get("my-logger")
      ```
---

The <span class="notranslate">Loggers</span> module provides you access to the server's
[Logger](/module/Logger) instances.
