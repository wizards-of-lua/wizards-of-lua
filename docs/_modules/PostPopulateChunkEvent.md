---
name: PostPopulateChunkEvent
title: PostPopulateChunkEvent
subtitle:
type: event
extends: PopulateChunkEvent
layout: module
properties:
functions:
---

Instances of the <span class="notranslate">PostPopulateChunkEvent</span> are fired when a new
chunk has been loaded and then been populated with blocks, ores, structures, and enities.

#### Example

Printing all <tt>PostPopulateChunkEvent</tt>s when they occur.

```lua
Events.on('PostPopulateChunkEvent'):call(function(event)
  print(string.format("Populated world chunk at %s,%s", event.chunkX, event.chunkZ))
end)
```
