---
title: Tutorials
layout: default
---
<ul>
{% assign tutorials = site.tutorials | sort: 'level' %}
{% for tutorial in tutorials %}
  <li>
    <a href="{{ tutorial.url }}">{{ tutorial.title }}</a>&nbsp;&nbsp;&nbsp;
    {{ tutorial.excerpt | strip_html }} <a href="{{ tutorial.url }}"> Read&nbsp;more...</a>
  </li>
{% endfor %}
</ul>
