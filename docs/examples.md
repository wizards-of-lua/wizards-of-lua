---
title: Examples
---
*This section is dedicated to spells we want to share with you.*

If you have a nice spell you want to share with us, please give us a hint by
posting it on the [forum thread for the alpha release](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/2855015-wizards-of-lua-1-0-0-alpha-lua-programming-in).

<ul>
{% for example in site.examples %}
  <li>
    <a href="{{ example.url }}">{{ example.title }}</a>&nbsp;&nbsp;&nbsp;
    {{ example.excerpt | strip_html }} <a href="{{ example.url }}"> Read&nbsp;more...</a>
  </li>
{% endfor %}
</ul>
