# The {{ page.name }}
{% if page.properties %}
{% assign properties = page.properties | sort: 'name' %}  
{% endif %}
{% if page.functions %}
{% assign functions = page.functions | sort: 'name' %}
{% endif %}
