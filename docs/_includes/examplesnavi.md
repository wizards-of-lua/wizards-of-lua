<div id="naviLeft">
<h2>Examples</h2>
<ul>
{% for example in site.examples limit:7 %}
  <li>
    <a href="{{ example.url }}">{{ example.title }}</a>
  </li>
{% endfor %}
<li><a href="/examples.html">See All Examples...</a></li>
</ul>
</div>
