---
title: Spell Packs
---

A spell pack is a convenient way of sharing spells between wizards and server owners.
Technically it's a Wizards of Lua add-on that contains one or more Lua files bundled into a Jar file.

Server owners can add a spell pack to their server by dropping it into the mods folder as they would do with every other mod.

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
