---
title: The Art of Spell Crafting
---
# Welcome to the Home of the Wizards of Lua

The Wizards of Lua are a [community of programmers and their friends](members.md),
who want to spread the knowledge of programming to kids.
The Wizards of Lua are also some decent gamers
who want to give gamers the ability to create their own game contents.

And finally 'The Wizards of Lua' is the name of a Minecraft
modification that simply adds the **/lua** command to the game.

<h2>{{ site.data.navigation.docs_list_title }}</h2>
<ul>
   {% for item in site.data.navigation.docs %}
      <li><a href="{{ item.url }}" alt="{{ item.title }}">{{ item.title }}</a></li>
   {% endfor %}
</ul>

# News
<ul>
  {% for post in site.posts %}
    <li>
      <a href="{{ post.url }}">{{ post.title }}</a>
    </li>
  {% endfor %}
</ul>
