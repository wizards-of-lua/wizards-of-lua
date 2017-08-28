---
title: Tutorials
---
<ul>
{% for tutorial in site.tutorials reversed limit:7 %}
  <li>
    <a href="{{ tutorial.url }}">{{ tutorial.title }}</a>&nbsp;&nbsp;&nbsp;
    {{ tutorial.excerpt | strip_html }} <a href="{{ tutorial.url }}"> Read&nbsp;more...</a>
  </li>
{% endfor %}
</ul>
