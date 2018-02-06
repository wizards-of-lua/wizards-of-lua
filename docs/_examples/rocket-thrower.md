---
title:  "The Rocket Thrower"
date:   2017-10-23 8:00:00
categories: Magic Wands
excerpt_separator: <!--more-->
author: mickkay
---
Imagine you want to have a magic wand that fires an explosive rocket when you swing
it through the air.
<!--more-->
In order to make it nice looking, you decide to give it
the shape of a glowing arrow.

I've already prepared such an item. Here is a picture of it:

![The Rocket-Firing Magic Wand](/images/magic-wand-rocket-arrow-in-frame.jpg)

Here is, how you can create this nice little rocket thrower:
```lua
/lua i = Items.get("arrow"); i:putNbt({tag= {
  display = {
    Name = "Rocket Thrower",
    Lore = {"Lauches an explosive rocket"}
  },
  ench = { {id=999, lvl=1} }
}});
spell.owner.mainhand=i
```

Now we want to add some behaviour to this item.
For that we need to create two separate functions:
one for making the rocket fly, and the other for launching it
when you swing your arm.

Since we need to create two functions, please make sure that you are familiar with
[importing Lua files](/tutorials/importing_lua_files) into your spells.

## How to Create a Profile

To ensure that you can use the new functions from the command line, you just need
to put them into your personal profile.
A profile is a special file that will be automatically imported when you cast a spell
from the command line.

If you haven't created a profile already, the following steps show  how you can do it.
First, authenticate your browser by typing:
```
/wol browser login
```  
and then click on the new link that appears in the chat (you must press 'T' before you can click on the link).

Then create the profile with the built-in editor by typing:
```
/wol file edit profile.lua
```
and again click on the new link.

Now you can copy and paste the source code of the two functions shown below into the new file and save it using CRTL-S.

First, let's have a look at the function that can create a flying rocket.
## How To Create a Rocket

This can be done in a lot of different ways.
We just choose a simple one that creates a beam of smoke until it hits
some block. Then it explodes by spawning some primed tnt.

<a name="rocket" style="position:relative; top:-70px; display:block;"></a>
Here is the code:
```lua
function rocket(player)
  player         = player or spell.owner
  spell.pos      = player.pos + Vec3(0,player.eyeHeight,0)
                              + player.lookVec*1.5
  local velocity = player.lookVec * 0.1
  spell:execute("/playsound entity.firework.shoot master @a ~ ~ ~ 0.5")
  for i=1,1000 do
    spell.pos = spell.pos+velocity
    spell:execute("particle smoke ~ ~ ~ 0 0 0 0 2 force")
    if spell.block.material.solid then
      for i=1,3 do
        spell:execute("/summon tnt ~ ~ ~ {Primed:1}")
      end
      return
    end
    if i%20==0 then
      sleep(1)
    end
    if i%100==0 then
      spell:execute("/playsound entity.generic.burn master @a ~ ~ ~ 2")
    end
  end
end
```
When you have added this function to your profile (and saved it), you
can experiment with it from the command line.

Just find some place with a good view onto something you want to blow up.
Make sure to look at it directly.
Then cast the following spell:

```lua
/lua rocket()
```
This should fire a rocket.
So far, so good.

## How To Bind a Spell to an Event
Now we want to bind this spell to the [SwingArmEvent](/modules/SwingArmEvent).

To do this, we need some spell that observes this kind of event.
Whenever it occurs, a rocket should be fired.

Here is the function for this:
```lua
function rocketThrowerObserver()
  spell.name = "rocket-thrower-observer"
  local q = Events.connect("SwingArmEvent")
  while true do
    local e = q:next()
    if e.item.displayName == "Rocket Thrower" then
      local cmd = "/lua p=Entities.find('@p[name=%s]')[1]; rocket(p)"
      spell:execute(cmd, e.player.name)
    end
  end
end
```
Make sure to add it to your profile, and then call:
```lua
/lua rocketThrowerObserver()
```

If you want to stop this spell any time later, just type:
```
/wol spell break byName rocket-thrower-observer
```
This will break all spells with the specified name.

This is handy when you want to cast an updated version of this spell and you
want to make sure that not two versions of this spell are running at the same time.

Here is a video of the rocket thrower in action:
{% include youtube.md id="p7r68hYR264" %}
