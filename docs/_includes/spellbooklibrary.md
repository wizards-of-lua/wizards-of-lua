<div id="naviLeft">
<h2>Books</h2>
<ul>
   {% assign modules = site.modules | where_exp:"m", "m.title != 'TODO'" %}
   {% assign modulesAvail = modules | sort: 'title' %}
   {% for module in modulesAvail %}
   <li><a href="{{ module.url }}" alt="{{ module.title }}"><u>{{ module.title }}</u> - <i>{{ module.subtitle }}</i></a></li>
   {% endfor %}
</ul>
</div>
