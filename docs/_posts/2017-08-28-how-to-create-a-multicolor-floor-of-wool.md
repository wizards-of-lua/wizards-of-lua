---
title:  "How to Create a Multicolor Floor of Wool"
date:   2017-08-28 10:49:00
categories: share a spell
excerpt_separator: <!--more-->
author: mickkay
layout: post
---
I really like to to start the share-a-spell initiative to illustrate the power of spells.
I picked a small one that creates a floor of colored wool blocks.
Let me show you how this is done.
<!--more-->

![Multicolor Floor of Wool](/images/multicolor-floor-of-wool.png)

The alpha version is really a little bit restricted so far since its only strengths are
* the manipulation of the blocky aspects of the world
* and the execution of commands within spells.

However, this is quite a bit.
A smart wizard can already do things that are otherwise quite difficult to achive.

For now, let's concentrate on the manipulation of the blocky aspects.
For example, the following spell can create a big multicolor floor of wool in seconds:

```lua
w=10;
l=10;
colors={"white","orange","magenta","light_blue",
"yellow","lime","pink","gray","silver","cyan",
"purple","blue","brown","green","red","black"
};
start=spell.pos;
y=-1;
for x=1,w do
  for z=1,l do
    spell.pos=start+Vec3.from(x,y,z);
    color=colors[math.random(#colors)];
    spell.block=Blocks.get("wool"):withData({color=color});
  end;
end;
```
Please note that almost all statements are terminated with the semicolon character <tt>';'</tt>.
For normal Lua programs this is actually not neccessary.
But due the fact that the current version of WoL (1.0.0-alpha) has no support for importing
external Lua files, we have to insert the whole program into the single line of the chat or a command block.
Here we need the semicolons, so that Lua can separate the statements from each other.
Actually, I had to execute this spell using a command block since it is a little bit longer than the character limit of the chat (wich is 256).

The variables <tt>w</tt> and <tt>h</tt> stand for "width" and "height".
For the screenshot above I set them both to a value of 20.

You can easily modify the above spell to create a whole box of wool.
Do you see the line where the <tt>y</tt> is?
Instead of assigning a constant value we can use it as the loop variable in another loop.
```lua
w=10;
l=10;
h=10;
colors={"white","orange","magenta","light_blue",
"yellow","lime","pink","gray","silver","cyan",
"purple","blue","brown","green","red","black"
};
start=spell.pos;
for y=0,h-1 do
  for x=1,w do
    for z=1,l do
      spell.pos=start+Vec3.from(x,y,z);
      color=colors[math.random(#colors)];
      spell.block=Blocks.get("wool"):withData({color=color});
    end;
  end;
end;
```
The height of the box is defined by <tt>h</tt>.

Again I set the width, length, and height to 20 and casted the spell. Look at the results:

![Multicolor Floor of Wool](/images/multicolor-box-of-wool.png)

Just imagine, how long it would take to build this box by hand...
