---
name: ChunkLoadEvent
title: ChunkLoadEvent
subtitle:
type: event
extends: ChunkEvent
layout: module
properties:
functions:
---

The <span class="notranslate">ChunkLoadEvent</span> occurs when a world chunk is loaded into the
server's memory.

#### Example

Printing all <tt>ChunkLoadEvent</tt>s when they occur.

```lua
Events.on('ChunkLoadEvent'):call(function(event)
 print(string.format("Loaded world chunk at %s,%s", event.chunkX, event.chunkZ))
end)
```
