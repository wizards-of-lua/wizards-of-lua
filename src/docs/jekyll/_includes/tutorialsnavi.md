<div id="naviLeft">
<h2>Tutorials</h2>
<ul>
{% assign tutorials = site.tutorials | sort: 'level' %}
{% for tutorial in tutorials limit:7 %}
  <li>
    <a href="{{ tutorial.url }}">{{ tutorial.title }}</a>
  </li>
{% endfor %}
<li><a href="/tutorials.html">See All Tutorials...</a></li>
</ul>
</div>
