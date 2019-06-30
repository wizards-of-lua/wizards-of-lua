---
title:  "The Vertical Rails Spell"
date:   2017-09-08 16:00:00
categories: Rails
excerpt_separator: <!--more-->
author: mickkay
---

Spells are really great to add new game experience.
For example, how nice would it be to have vertical rails
for minecarts?
<!--more-->

Here is a short video that shows a spell that transforms ladders to vertical rails.

{% include youtube.md id="VWeUUNAbcpU" %}

Essentially the spell searches the world for all types of minecarts and checks if
they are close to a ladder.
If so, the minecart's motion property is updated so that it moves upwards.

This spell must be inserted into a command block.
As always, don't forget to add <tt>/lua </tt> in front of it.
```lua
/lua speed=0.1;
exitMotions = {
  north = {0, 0, 0.2},
  south = {0, 0, -0.2},
  west = {0.2, 0, 0},
  east = {-0.2, 0, 0}
};
function addAll(t1, t2)
  for k,v in ipairs(t2) do
    table.insert(t1, v)
  end;
end;
while true do
  local minecarts = Entities.find('@e[type=minecart]');
  addAll(minecarts, Entities.find('@e[type=chest_minecart]'));
  addAll(minecarts, Entities.find('@e[type=furnace_minecart]'));
  addAll(minecarts, Entities.find('@e[type=hopper_minecart]'));
  addAll(minecarts, Entities.find('@e[type=spawner_minecart]'));
  addAll(minecarts, Entities.find('@e[type=tnt_minecart]'));

  for k, minecart in pairs(minecarts) do
    spell.pos = minecart.pos;
    if spell.block.name == 'ladder' then
      local blockX = math.floor(minecart.pos.x) + 0.5;
      local blockZ = math.floor(minecart.pos.z) + 0.5;
      local motion = {blockX - minecart.pos.x, speed, blockZ - minecart.pos.z};
      minecart:putNbt({Motion = motion});
    else --[[ The else part just gives the minecart a little boost when exiting a ladder ]]
      spell.pos = spell.pos - Vec3(0, 0.5, 0);
      if spell.block.name == 'ladder' then
        local motion = exitMotions[spell.block.data.facing];
        minecart:putNbt({Motion = motion});
      end;
    end;
  end;
  sleep(1);
end
```
When activated this spell loops until you manually break it.

## Credits
This spell was created by Adrodoc55. Many thanks for that!

It was inspired by an amazing command block setup published by *Sparks* of *Gamemode 4*.

If you are curious, here is a video of it.
{% include youtube.md id="LJoN7CmJL4Q" %}
