---
title: Using the str() function
excerpt_separator: <!--more-->
author: mickkay
level: 1
---
This tutorial shows how you can use the <tt>str()</tt> function to get
a glance of the internal structure of an object.
This is especially useful when you don't know how this object is structured.
<!--more-->

For example, let's pretend you are looking at the trunk of a birch tree.

![Looking at a Birch](/images/looking-at-a-birch.jpg)

You wonder, how this nice block is called in Minecraft.
But sad to say, you don't know how to get the name of a block using Lua.

When you execute a spell like this:
```lua
/lua print( spell.block)
```
you just get something like the following not very meaningful output:
```lua
table: 0x0626bef0
```
What you see is the standard output format of a table in Lua.
This is because the underlying representation of a [Block](/modules/Block/) in Lua is a table.

So this is not helpful.
But we can do better.
By using the <tt>str()</tt> function you can get a string with the table's contents
in a human readable way.
```lua
/lua print( str( spell.block))
```
Now you get this:
```lua
{
  data = {
    axis = "y",
    variant = "birch"
  },
  material = {
    blocksLight = true,
    blocksMovement = true,
    canBurn = true,
    liquid = false,
    mobility = "NORMAL",
    opaque = true,
    replaceable = false,
    requiresNoTool = true,
    solid = true
  },
  name = "log"
}
```
Now you can see the internal structure of the table, which is just a list of properties.
The properties are enclosed in curly braces and separated by commas.
Each property consists of a key-value pair.
For example, at the very bottom there is the "name" property.
Its key is "name" and its value is "log".
You may have noticed that the value is enclosed in quotation marks, while for the key the quotation marks are omitted.
Because of this the output of the str() function is very handy, since it forms valid Lua code when used in an assignment.

You can also see that the properties have a tree-like structure.
For example, the "data" property has a value that itself is a table.


Now that you know the internal structure of the Block, you can rewrite the spell
so that it only prints the part you are interested in, e.g. its name and its variant.
```lua
/lua print( spell.block.name, spell.block.data.variant)
```
Which gives you:
```lua
log birch
```

Btw. instead of <tt>str()</tt> you could alternativly use the <tt>inspect()</tt> function, which will also give you the contents of the table's metatable.
