<div id="naviRight">
<h2>{{ site.data.navigation.title }}</h2>

<h3>{{ site.data.navigation.general.title }}</h3>
<ul>
   {% for item in site.data.navigation.general.items %}
      <li><a href="{{ item.url }}" alt="{{ item.title }}">{{ item.title }}</a></li>
   {% endfor %}
</ul>

<h3>{{ site.data.navigation.versioned.title }}</h3>
<ul>
   {% for item in site.data.navigation.versioned.items %}
      <li><a href="{{ item.url }}" alt="{{ item.title }}">{{ item.title }}</a></li>
   {% endfor %}
</ul>

</div>
