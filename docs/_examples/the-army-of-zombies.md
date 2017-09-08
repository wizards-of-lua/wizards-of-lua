---
title:  "The Army of Zombies"
date:   2017-08-29 18:00:00
categories: Mobs
excerpt_separator: <!--more-->
author: mickkay
---

A wizard who knows not only Lua but also vanilla Minecraft commands, can combine
both to very powerful spells. An example is the "Army of Zombies" spell.
<!--more-->

![Army of Zombies](/images/army-of-zombies.jpg)
Aren't they nice?

To create an army of zombies you should be familiar with the Minecraft
<tt>/summon</tt> command and the possibilities of the entity data tag.
But well, even experienced wizards have to cheat from time to time.
There are plenty of websites out there where you can do a quick lookup.
For example, you could use the [Summon Command generator from minecraftupdates.com](http://www.minecraftupdates.com/summon-command)
to get the following summon command for a single zombie soldier:
```
/summon Zombie ~0 ~0 ~0 {HandItems:[{Count:1,id:diamond_sword},
{Count:1,id:shield}],ArmorItems:[{Count:1,id:leather_boots},{},
{Count:1,id:iron_chestplate},{Count:1,id:iron_helmet}],
CustomName:soldier,CustomNameVisible:0,NoAI:1}
```

Armed with this command you can start to create a new Lua spell.
If you are familliar with the books found in the [Spell Book Library](/spellbooklibrary)
you already know the powerful [Spell's execute](/modules/Spell/#execute) function.
If not, well, here is what it does.

The Spell's execute function can execute any Minecraft command.
For example, you could write this:
```lua
spell:execute("/say hi")
```
This will make your spell say "hi".

The next step is to write a Lua spell that creates this neatly arranged
rows of soldiers.
This is really easy.
You just have to enclose the execute statement in a double for-loop.

```lua
summon="/summon Zombie ~0 ~0 ~0 {HandItems:[
{Count:1,id:diamond_sword},{Count:1,id:shield}],
ArmorItems:[{Count:1,id:leather_boots},{},
{Count:1,id:iron_chestplate},{Count:1,id:iron_helmet}],
CustomName:soldier,CustomNameVisible:0,NoAI:1}";
s=spell.pos;
for x=1,5 do
  for z=1,5 do
    spell.pos=s+Vec3(x*2,0,z*2);
    spell:execute(summon);
  end;
end;
```
As you can see I stored the summon command into a variable so that I could use
it later as a parameter for the execute function.
This is very helpful since it increases the readability of the spell's code.

On a side note: I created brainless zombies, because I
needed them to stand still for my little photo session.
However, if you want them to walk around again you can 'brainify' them any time later with the following command:
```/entitydata @e[name=soldier] {NoAI:0}```
