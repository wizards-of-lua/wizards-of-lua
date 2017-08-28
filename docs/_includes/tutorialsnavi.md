<div id="naviLeft">
<h2>Tutorials</h2>
<ul>
{% for tutorial in site.tutorials limit:7 %}
  <li>
    <a href="{{ tutorial.url }}">{{ tutorial.title }}</a>
  </li>
{% endfor %}
<li><a href="/tutorials.html">See All Tutorials...</a></li>
</ul>
</div>
