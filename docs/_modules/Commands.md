---
name: Commands
subtitle: Managing Custom Commands
type: module
layout: module
properties:
functions:
  - name: register
    parameters: name, code, usage
    results: nil
    description: |
      Registers a new custom command with the given name and the given Lua code (provided as text).
      Optionally accepts the usage string as a third parameter.
      The Lua code will be compiled every time when the command is issued.

      The command stays registered until it is deregistered or the server is being restarted.

      #### Example

      Registering a command called <span class="notranslate">"home"</span> that teleports the calling
      player to his last known spawn point, or otherwise to the world spawn.

      ```lua
      Commands.register("home",[[
        local p = spell.owner
        local n = p.nbt
        if n.SpawnX then
          p.pos = Vec3(n.SpawnX, n.SpawnY, n.SpawnZ)
        else
          p.pos = p.world.spawnPoint
        end
      ]])
      ```

      #### Example

      Registering a command called <span class="notranslate">"health"</span> that can set the health
      of the specified player to the specified value.

      ```lua
      Commands.register("health",[[
        local name,value = ...
        local p=Entities.find("@a[name="..name.."]")[1]
        if p then
          p.health=tonumber(value)
        else
          print("player not found")
        end
      ]], "/health <player> <new health>")
      ```

  - name: deregister
    parameters: name
    results: nil
    description: |
      Deregisters the command with the given name. This function can only deregister custom commands
      that have been registered with 'register'.

---

The <span class="notranslate">Commands</span> module allows the registration of custom commands than
can be used from the chat line.
