{% if page.extends %}
{% assign modules = site.modules | where_exp:"m", "m.name == page.extends" %}
The {{ page.name }} class is a subtype of the <a href="{{ modules[0].url }}">{{ modules[0].name }}</a> class and inherits all its properties and functions.
{% endif %}
{% if page.properties %}
Here is a brief list of the {{ page.name }}'s *properties*:

| Property             | Type          | read / write |
| ---------------------|---------------| :-----------:|
{% for prop in properties %}| [{{ prop.name }}](#{{ prop.name }}) | {{ prop.type | replace: '!SITE_URL!', site.url }} | {{ prop.access }} |
{% endfor %}
{% endif %}
{% if page.functions %}
Here is a brief list of the {{ page.name }}'s *functions*:

| Function             | Parameters    | Results      |
| ---------------------|---------------| :-----------:|
{% for func in functions %}| [{{ func.name }}](#{{ func.name }}) | {{ func.parameters | replace: '!SITE_URL!', site.url }} | {{ func.results | replace: '!SITE_URL!', site.url }} |
{% endfor %}
{% endif %}

{% if page.properties %}
## Properties

Below you find short descriptions about each of the {{ page.name }}'s properties
and some examples about how to used them in your spells.

---
{% for prop in properties %}
<a style="position:relative; top:-70px; display:block;" name="{{ prop.name }}"></a>
### {{ prop.name }} : {{ prop.type | replace: '!SITE_URL!', site.url }}

{{ prop.description | replace: '!SITE_URL!', site.url}}
{% for ex in prop.examples %}
{% include_relative {{ ex.url }} %}
{% endfor %}
---
{% endfor %}
{% endif %}

{% if page.functions %}
## Functions

Below you find short descriptions about each of the {{ page.name }}'s functions
and some examples about how to used them in your spells.

---
{% for func in functions %}
<a style="position:relative; top:-70px; display:block;" name="{{ func.name }}"></a>
### {{ func.name }} ({{ func.parameters | replace: '!SITE_URL!', site.url }}) -> {{ func.results | replace: '!SITE_URL!', site.url }}

{{ func.description | replace: '!SITE_URL!', site.url}}
{% for ex in func.examples %}
{% include_relative {{ ex.url }} %}
{% endfor %}
---
{% endfor %}
{% endif %}
