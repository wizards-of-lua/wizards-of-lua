<ul>
  {% for post in site.posts %}
    <li>
      <a href="{{ post.url }}">{{ post.title }}</a>&nbsp;&nbsp;&nbsp;
      {{ post.excerpt | strip_html }} <a href="{{ post.url }}"> Read&nbsp;more...</a>
    </li>
  {% endfor %}
</ul>
