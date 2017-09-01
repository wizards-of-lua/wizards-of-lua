---
title:  "The Door"
date:   2017-09-01 10:00:00
categories: Summoning
excerpt_separator: <!--more-->
author: mickkay
---

Making new doors in Minecraft is difficult unless you know some magic.
This examples shows how this can be done with two little spells.
<!--more-->

Here is a short video that shows that door in action.

<iframe width="650" height="400" src="https://www.youtube.com/embed/rC6ouNtisAA" frameborder="1" allowfullscreen></iframe>

<br/>
To make a door like this, place two command blocks in a distance of
4 blocks from each other.
The left one is for opening the door, the right one for closing it.
Place two buttons above each command block, one from the inside the
cave, one from outside.

Here is a picture of it.

![The Door](/images/the-door.jpg)

## Opening the Door
This spell must be inserted into the left command block.
It opens the door.

```lua
s=spell.pos;
b=Blocks.get("air");
for y=0,3 do
  for z=1,4 do
    spell.pos=s+Vec3(0,y,-z);
    if spell.block.name~="air" then
      spell.block=b;
      spell:execute("particle blockcrack ~ ~ ~ 0.2 0.2 0.2 0 20");
    end;
  end;
end
```

## Closing the Door
This spell must be inserted into the right command block.
It closes the door.

```lua
s=spell.pos;
b=Blocks.get("nether_brick_fence");
for y=0,3 do
  for z=1,4 do
    spell.pos=s+Vec3(0,y,z);
    spell.block=b;
    spell:execute("particle largesmoke ~ ~ ~ 0.2 0.2 0.2 0 10");
  end;
end
```
