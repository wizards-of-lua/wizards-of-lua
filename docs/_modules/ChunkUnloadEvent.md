---
name: ChunkUnloadEvent
title: ChunkUnloadEvent
subtitle:
type: event
extends: ChunkEvent
layout: module
properties:
functions:
---

The <span class="notranslate">ChunkUnloadEvent</span> occurs when a world chunk is unloaded from
the server's memory.

#### Example

Printing all <tt>ChunkUnloadEvent</tt>s when they occur.

```lua
Events.on('ChunkUnloadEvent'):call(function(event)
 print(string.format("Unloaded world chunk at %s,%s", event.chunkX, event.chunkZ))
end)
```
