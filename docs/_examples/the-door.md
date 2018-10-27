---
title:  "The Door"
date:   2017-09-01 10:00:00
categories: Doors
excerpt_separator: <!--more-->
author: mickkay
---

Making new doors in Minecraft is difficult unless you know some magic.
This examples shows how this can be done with two little spells.
<!--more-->

Here is a short video that shows that door in action.

{% include youtube.md id="rC6ouNtisAA" %}

<br/>
To make a door like this, place two command blocks in a distance of
4 blocks from each other.
Make sure that both command blocks are pointing towards the outside of the cave.

Here is a picture of it.

![The Door](/images/the-door.jpg)

Place two buttons above each command block, one from the inside the
cave, one from outside.

## Closing the Door
This spell must be inserted into the right command block as seen from outside the cave.
It closes the door.

```lua
/lua spell:move("right");
local stone = Blocks.get("nether_brick_fence");
spell:move("up",3);
for k=1,4 do
  for i=1,4 do
    if spell.block.name~=stone.name then
      spell.block = stone;
      spell:execute("particle largesmoke ~ ~ ~ 0.2 0.2 0.2 0 10");
      spell:execute("playsound minecraft:block.iron_door.close block @a ~ ~ ~ 1");
    end;
    spell:move("right");
  end;
  sleep(2);
  spell:move("left",4);
  spell:move("down");
end;
```

## Opening the Door
This spell must be inserted into the left command block.
It opens the door.

```lua
/lua spell:move("left");
local air = Blocks.get("air");
for k=1,4 do
  for i=1,4 do
    if spell.block.name~=air.name then
      spell.block = air;
      spell:execute("particle blockcrack ~ ~ ~ 0.2 0.2 0.2 0 20 normal @a 113");
      spell:execute("playsound minecraft:block.iron_door.open block @a ~ ~ ~ 1");
    end;
    spell:move("left");
  end;
  sleep(2);
  spell:move("right",4);
  spell:move("up");
end;
```
