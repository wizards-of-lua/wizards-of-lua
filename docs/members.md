---
title: The Creators
layout: default
---
{% assign foundingMembers = site.data.members | where_exp:"m", "m.category=='founding-member'" %}
{% if foundingMembers != empty %}
{% for member in foundingMembers %}
{% assign member = entry[1] %}
* <a href="https://github.com/{{ member.github }}">
      {{ member.name }}{% if member.title %}, {{ member.title }}{% endif %}
    </a>
    {% if member.roles %}({{ member.roles }}){% endif %}
{% endfor %}
{% endif %}

{% assign contributors = site.data.members | where:"m", "m.category=='contributor'" %}
{% if contributors != empty %}
## Contributors
{% assign contributorsSorted = contributors | sort:"name" %}
{% for member in contributorsSorted %}
* <a href="https://github.com/{{ member.github }}">
      {{ member.name }}{% if member.title %}, {{ member.title }}{% endif %}
    </a>
    {% if member.roles %}({{ member.roles }}){% endif %}
{% endfor %}
{% endif %}
