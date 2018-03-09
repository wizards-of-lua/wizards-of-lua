---
name: System
subtitle: Interacting with the Server's OS
type: module
layout: module
properties:
functions:
  - name: execute
    parameters: name, args...
    results: exitcode, string
    description: |
      The 'execute' function invokes the program with the given name and the given arguments
      on the server's operating system. Please note that you only can execute programs living inside the
      server's [script gateway directory](/configuration-file).
      Please note that this is a blocking call.
      The spell will resume its execution only after the program has terminated.

      #### Example
      Calling the "echo.sh" shell script from the server's script gateway directory.
      ```lua
      exitcode, result = System.execute("echo.sh","some argument")
      print("exitcode", exitcode)
      print("result", result)
      ```
---

The <span class="notranslate">System</span> module provides functions for interacting with the server's operating system.
