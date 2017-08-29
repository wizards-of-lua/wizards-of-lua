<div id="naviLeft">
<h2>Share a Spell</h2>
<ul>
{% for spell in site.spells limit:7 %}
  <li>
    <a href="{{ spell.url }}">{{ spell.title }}</a>
  </li>
{% endfor %}
<li><a href="/spells.html">See All Shared Spells...</a></li>
</ul>
</div>
