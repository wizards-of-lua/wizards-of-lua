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

This tutorial shows
* how to bind an individual spell into an arbitrary item
* and how to create an observer spell that activates it.

Please note, that this tutorial assumes that you are familiar with
[importing Lua files](/tutorials/importing_lua_files) into your spells.

Normally, if you have three different magic wands, for example, the wand of feather-light flight,
the [rocket thrower](/examples/rocket-thrower), and the smoking stick, you also need
three distinct observer spells, each observing the event source for the [swing arm events](/modules/SwingArmEvent),
and if that event occurs, then casting the appropriate spell.
But creating an observer spell for each type of magic wand is cumbersome.
Instead, we want to have one single observer spell for all kind of wands.

But first, let's understand, what a typical observer spell is doing,
for example, by looking at the rocket thrower observer:
```lua
function rocketThrowerObserver()
  local q = Events.collect("SwingArmEvent")
  while true do
    local e = q:next()
    if e.item.displayName == "Rocket Thrower" then
      local cmd = "/lua p=Entities.find('@p[name=%s]')[1]; rocket(p)"
      spell:execute(cmd, e.player.name)
    end
  end
end
```
As you can see, whenever a SwingArmEvent occurs, this observer checks if the item's name is
correct (here: Rocket Thrower), and if so, it will call the corresponding function (here:
[rocket()](/examples/rocket-thrower#rocket) ) with the player object passed in as argument.

Now that we know what the observer spell is doing, we want to disassemble it into two parts.
Part one will be responsible for providing the wand-specific command.
And part two will become the new, centralized observer spell, that can handle all magic
wands together in one place.

To make it work, we need a place where we can look up the wand-specific command, as
we want to cast different spells for different wands.
A very nice solution is to store the wand's specific spell into the wand itself,
or more precisely the wand's nbt data.

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
But this is not enough.
We still need another spell that observes the events and handles them by
executing the commands.

## A Centralized Observer Spell for Magic Wands
The centralized observer spell is responsible for handling all
swing arm events by calling the item-specific command that has been stored inside
the item.

It looks like this:
```lua
function magicWandObserver()
  local q=Events.collect("SwingArmEvent")
  while true do
    local   e = q:next()
    local nbt = e.item.nbt
    if nbt and nbt.tag and nbt.tag.OnSwingArmEvent then
      local pname = e.player.name
      local itemCommand = nbt.tag.OnSwingArmEvent
      spell:execute("/execute %s ~ ~ ~ %s", pname, itemCommand)
    end
  end
end
```
As you can see, this spell extracts the item-specific command from the item's
nbt data and passes it into the ["/execute" command](https://minecraft.gamepedia.com/Commands/execute),
which is part of vanilla Minecraft.
This ensures that the new spell will be casted in behalf of the player who just waved the magic wand.

That's about all. To cast your centralized observer spell, just type this:
```lua
/lua magicWandObserver()
```
