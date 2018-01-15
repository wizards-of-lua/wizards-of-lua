---
title: Binding a Spell to a Magic Wand
excerpt_separator: <!--more-->
author: mickkay
level: 10
---
What is a wizard without a collection of magic wands?


It is nice to have a collection, where every wand is bound to a unique spell.
But writing an observer spell for every type of magic wand is cumbersome.
Instead, it would be nice to have only one single observer spell
that can handle all magic wands. To achive this, you just need to store the
magic spell into the item itself.
<!--more-->
This tutorial shows how to bind an individual spell into an arbitrary item
and how to create an observer spell that activates it.

Please note, that this tutorial assumes that you are familiar with
[importing Lua files](/tutorials/importing_lua_files) into your spells.

Normally, if you have three different magic wands, for example, a flying feather,
a [rocket thrower](/examples/rocket-thrower), and a smoke stick, you need three distinct observer spells,
each observing the event source for the [swing arm events](/modules/SwingArmEvent).
This is cumbersome.
To change this, we want to have one single observer spell for all kind of wands.

But first, let's have a look into a typical observer spell,
for example, the rocket thrower observer:
```lua
function rocketThrowerObserver()
  local q = Events.connect("SwingArmEvent")
  while true do
    local e = q:pop()
    if e.item.displayName == "Rocket Thrower" then
      local cmd = "/lua p=Entities.find('@p[name=%s]')[1]; rocket(p)"
      spell:execute(cmd, e.player.name)
    end
  end
end
```
Whenever a SwingArmEvent occurs, this observer checks if the item's name is
correct, and then it will call the actual [rocket()](/examples/rocket-thrower#rocket) function with the
player object passed in as argument.

What we want to do next, is to disassemble this spell into two parts.
One part will be responsible for providing the wand-specific command.
The other part will become the new, centralized observer spell, that can handle all magic
wands together in one place.

For this to work, we need a place where we can look up the wand-specific command, as
we want to cast different spells for different wands.
A very nice solution is to store the wand's specific spell into the
item's nbt data.

## How To Bind a Command to an Item
Luckily, this is really easy.
Let's thank the great Adrodoc55, who has discovered this smart solution.
The following function stores the given command into the given item.
```lua
-- This binds the given command to the given item
function bind(command, item)
  item = item or spell.owner.mainhand
  if not instanceOf(Item,item) then
    error("Expected Item, but got %s", type(item))
  end
  item:putNbt( {
    tag = {
      OnSwingArmEvent = command
    }
  })
end
```
Essentially, this spell adds a custom named attribute called "OnSwingArmEvent"
to the item's tag list, with the given command as its value.

For example, to bind the rocket spell to an item, we just need to take
that item into our main hand and call:
```lua
/lua bind("/lua rocket()", spell.owner.mainhand)
```

We have just defined what should be done when the
[SwingArmEvent](/modules/SwingArmEvent) occurs.
But this is not enough, is it?
We still need another spell that observes the events and handles them by
executing the commands.

## A Centralized Observer Spell for Magic Wands
The centralized observer spell is resposible for handling all
swing arm events by calling the item-specific command that has been stored inside
the item.

It looks like this:
```lua
function magicWandObserver()
  local q=Events.connect("SwingArmEvent")
  while true do
    local   e = q:pop()
    local nbt = e.item.nbt
    if nbt and nbt.tag and nbt.tag.OnSwingArmEvent then
      local pname = e.player.name
      local cmd = "/execute %s ~ ~ ~ %s"
      spell:execute(cmd, pname, nbt.tag.OnSwingArmEvent)
    end
  end
end
```
As you can see, this spell extracts the item-specific command from the item's
nbt data and passes it into a new vanialla "/execute" command.
This ensures, that the new spell will be casted in behalf of the correct player,
which is the player who waved the magic wand.

Of course that implies, that the new spell must be able to access all modules and
functions that are referenced by its code.
To ensure this, you must import these modules into the spell's environment by
using the <tt>require</tt> function.
For that to work, you either need to place the modules into the shared library directory,
or, if the module is inside a player-specific library, you need to extend the
Lua search path by using the <tt>addpath()</tt> function.
If you are unfamiliar with this, please have a look into our
[tutorial about importing Lua files](/tutorials/importing_lua_files).

So, that's it.

If you want to make your magic wands work, just start your centralized observer:
```lua
/lua magicWandObserver()
```
