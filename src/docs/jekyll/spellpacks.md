---
title: Spell Packs
layout: default
---

A spell pack is a convenient way of sharing spells between wizards and server owners.
Technically it's a Wizards of Lua add-on that contains one or more Lua files bundled into a Jar file.

Server owners can add a spell pack to their server as any "normal" Forge-based Minecraft modification by dropping it into the mods folder. When placed into the server's mods folder (next to the WoL JAR file) the spell pack will be added to the Lua search path.

Spell packs can be created by using the [/wol pack export](/versions/current/wol-command#pack) command.

Here is a list of featured spell packs:


<ul>
{% assign packs = site.spellpacks | sort: 'order' %}
{% for pack in packs %}
  <li>
    <a href="{{ pack.url }}">{{ pack.title }}</a>&nbsp;&nbsp;&nbsp;
    {{ pack.excerpt | strip_html }} <a href="{{ pack.url }}"> Read&nbsp;more...</a>
  </li>
{% endfor %}
</ul>
