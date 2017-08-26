---
title: The Art of Spell Crafting
---
# Welcome to the Home of the Wizards of Lua

![Manison](images/manison.jpg){:width="400px"}

The Wizards of Lua are a community of programmers and their friends
who want to promote interactive programming into a new level
of game experience.

The Wizards of Lua is also a Minecraft modification that simply adds
the ```/lua``` command to the game.
Right  this command you can enter sequence of statements that form
a valid Lua program.

For example, you could enter the following line into the chat:
```lua
/lua for i=1,10 do place("stone"); move(UP); end
```
This will create a pillar of stone directly in front of you, 10 meters tall.

![Pillar of Stone](images/pillar-of-stone.jpg)

# API Documentation
It would be great if we could show some API documeentation here.
This is a sample page with the [Spell's API documentation](Spell-api.md).

# Blog Posts
<ul>
  {% for post in site.posts %}
    <li>
      <a href="{{ post.url }}">{{ post.title }}</a>
      {{ post.excerpt }}
    </li>
  {% endfor %}
</ul>

# images
{% assign image_files = site.static_files | where: "image", true %}
{% for myimage in image_files %}
  {{ myimage.path }}
  <img src="{{myimage.path}}"/>
{% endfor %}

# Members
<ul>
{% for member in site.data.members %}
  <li>
    <a href="https://github.com/{{ member[1].github }}">
      {{ member[1].name }}
    </a>
  </li>
{% endfor %}
</ul>

# Lua Modules API
{% assign modules = site.modules %}
{% for module in modules %}

## <a href="{{ module.url }}">{{ module.name }}</a>
{% for prop in module.properties %}
### {{ prop.name }} : {{ prop.type }}
{% endfor %}
{% endfor %}
