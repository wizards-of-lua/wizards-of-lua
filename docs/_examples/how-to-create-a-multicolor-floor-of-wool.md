---
title:  "How to Create a Multicolored Floor of Wool"
date:   2017-08-28 10:49:00
categories: Structures
excerpt_separator: <!--more-->
author: mickkay
---
To illustrate the power of spells I like to show you a small one that creates a floor of colored wool blocks.
<!--more-->

![Multicolored Floor of Wool](/images/multicolor-floor-of-wool.jpg)

At the time of writing, the alpha version (1.0.0-alpha) is somewhat limited since its only strengths are
* the manipulation of the blocky aspects of the world
* and the execution of commands within spells.

However, this is quite something, since a smart wizard already can do things that are otherwise difficult to achieve.

For now, let's concentrate on the manipulation of blocks.
For example, the following spell can create a big multicolored floor of wool in seconds.

```lua
/lua w=10;
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

For normal Lua programs this is actually not necessary.
But due the fact that the current version of WoL (1.0.0-alpha) has no support for
[importing Lua files](/tutorials/importing_lua_files), we have to insert the whole program into the single line of the chat or a command block.
Here we need the semicolons, so that Lua can separate the statements from each other.
Actually, I had to execute this spell using a command block since it is a little bit longer than the character limit of the chat (wich is 256).

The variables <tt>w</tt> and <tt>l</tt> stand for "width" and "length".
For the screenshot above I set them both to a value of 20.

You can easily modify the above spell to create a whole box of wool.
Do you see the line where the <tt>y</tt> is?
Instead of assigning a constant value we can use it as the loop variable in another loop.
```lua
/lua w=10;
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

![Multicolored Box of Wool](/images/multicolor-box-of-wool.jpg)

Just imagine how long it would take to build this box by hand...
