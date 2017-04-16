{% if page.properties %}
Here is a brief list of the {{ page.name }}'s *properties*:

| Property             | Type          | read / write |
| ---------------------|---------------| :-----------:|
{% for prop in properties %}| [{{ prop.name }}](#{{ prop.name }}) | {{ prop.type }} | {{ prop.access }} |
{% endfor %}
{% endif %}
{% if page.functions %}
Here is a brief list of the {{ page.name }}'s *functions*:

| Function             | Parameters    | Results      |
| ---------------------|---------------| :-----------:|
{% for func in functions %}| [{{ func.name }}](#{{ func.name }}) | {{ func.parameters }} | {{ func.results }} |
{% endfor %}
{% endif %}

{% if page.properties %}
## Properties

Below you find a short description about each of the {{ page.name }}'s properties
and some examples about how to used them in your spells.

---
{% for prop in properties %}
<a name="{{ prop.name }}"></a>
### {{ prop.name }} : {{ prop.type }}
{{ prop.description }}
{% for ex in prop.examples %}
{% include_relative {{ ex.url }} %}
{% endfor %}
---
{% endfor %}
{% endif %}

{% if page.functions %}
## Functions

Below you find a short description about each of the {{ page.name }}'s functions
and some examples about how to used them in your spells.

---
{% for func in functions %}
<a name="{{ func.name }}"></a>
### {{ func.name }} ({{ func.parameters }}) -> {{ func.results }}
{{ func.description }}
{% for ex in func.examples %}
{% include_relative {{ ex.url }} %}
{% endfor %}
---
{% endfor %}
{% endif %}
