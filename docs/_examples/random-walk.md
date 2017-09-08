---
title:  "Random Walking Grass"
date:   2017-08-28 18:00:00
categories: Moving Blocks
excerpt_separator: <!--more-->
author: mickkay
---

With a spell it's really easy to visualize a 2-dimensional [random walk](https://en.wikipedia.org/wiki/Random_walk).
<!--more-->

Here is a photo of the remains left by a random walking grass block.

![Random Walking Grass](/images/random-walk-in-dessert.jpg)

Just paste the following source code into a command block.
As always: don't forget to add ```/lua ``` in front of it.

```lua
grass=Blocks.get("grass");
dir={"north","south","east","west"};
for i=1,200 do
  spell:move(dir[math.random(#dir)]);
  while spell.block.material.solid do
    spell:move("up");
  end;
  spell:move("up");
  while spell.block.material.solid==false do
    spell:move("down");
  end;
  spell:move("up");
  spell:execute("particle largesmoke ~ ~ ~ 0.3 0.3 0.3 0 4");
  sleep(2);
  spell.block=grass;
end;
```

Since we are in a 3-dimensional world we have to make sure that the new
block is always placed on the topmost place.
That's why we have those two <tt>while</tt>-loops in the middle of the spell.
On the bottom you can see the usage of the Spell's very powerful [execute](/modules/Spell/#execute) function
that can execute any Minecraft command.
Here we use it to create some fancy particle effects.

Now attach a button to the command block and press it.
