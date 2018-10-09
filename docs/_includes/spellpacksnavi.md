<div id="naviLeft">
<h2>Spell Packs</h2>
<ul>
{% assign packs = site.spellpacks | sort: 'order' %}
{% for pack in packs limit:7 %}
  <li>
    <a href="{{ pack.url }}">{{ pack.title }}</a>
  </li>
{% endfor %}
<li><a href="/spellpacks.html">See All Spell Packs...</a></li>
</ul>
</div>
