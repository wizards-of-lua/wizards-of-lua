<div id="naviLeft">
<h2>Modules</h2>
<ul>
   {% assign modules = site.modules | where_exp:"m", "m.type == 'module'" %}
   {% assign modulesAvail = modules | sort: 'title' %}
   {% for module in modulesAvail %}
   {% if module.subtitle %}
   {% capture sub %} - {{ module.subtitle }}{% endcapture %}
   {% else %}
   {% assign sub = "" %}
   {% endif %}
   <li><a href="{{ module.url }}" alt="{{ module.title }}"><u>{{ module.title }}</u><i>{{ sub }}</i></a></li>
   {% endfor %}
</ul>
<h2>General Classes</h2>
<ul>
   {% assign modules = site.modules | where_exp:"m", "m.type == 'class'" %}
   {% assign modulesAvail = modules | sort: 'title' %}
   {% for module in modulesAvail %}
   {% if module.subtitle %}
   {% capture sub %} - {{ module.subtitle }}{% endcapture %}
   {% else %}
   {% assign sub = "" %}
   {% endif %}
   <li><a href="{{ module.url }}" alt="{{ module.title }}"><u>{{ module.title }}</u><i>{{ sub }}</i></a></li>
   {% endfor %}
</ul>
<h2>Events</h2>
<ul>
   {% assign modules = site.modules | where_exp:"m", "m.type == 'event'" %}
   {% assign modulesAvail = modules | sort: 'title' %}
   {% for module in modulesAvail %}
   {% if module.subtitle %}
   {% capture sub %} - {{ module.subtitle }}{% endcapture %}
   {% else %}
   {% assign sub = "" %}
   {% endif %}
   <li><a href="{{ module.url }}" alt="{{ module.title }}"><u>{{ module.title }}</u><i>{{ sub }}</i></a></li>
   {% endfor %}
</ul>

</div>
