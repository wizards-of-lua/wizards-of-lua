<div id="naviLeft">
<h2>News</h2>
<ul>
{% for post in site.posts limit:7 %}
  <li>
    <a href="{{ post.url }}">{{ post.date | date_to_long_string}} -<br/>  {{ post.title }}</a>
  </li>
{% endfor %}
</ul>
<a href="/news.html">Read All News...</a>

</div>
