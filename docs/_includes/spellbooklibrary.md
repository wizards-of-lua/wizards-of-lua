<div id="naviLeft">
<h2>Spell Book Library</h2>
<ul>
   {% assign modules = site.modules | where_exp:"m", "m.title != 'TODO'" %}
   {% assign modulesAvail = modules | sort: 'name' %}
   {% for module in modulesAvail %}
   <li><a href="{{ module.url }}" alt="{{ module.name }}">{{ module.name }}:<br> {{ module.title }}</a></li>
   {% endfor %}
</ul>
</div>
