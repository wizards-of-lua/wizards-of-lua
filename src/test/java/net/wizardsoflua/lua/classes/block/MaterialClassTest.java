package net.wizardsoflua.lua.classes.block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import net.minecraft.block.material.Material;
import net.wizardsoflua.testenv.TestDataFactory;

public class MaterialClassTest extends TestDataFactory {
  @Test
  public void test_all_material_names_are_mapped() throws Exception {
    // Given:
    Map<Material, String> expectedNames = new LinkedHashMap<>();
    Field[] fields = Material.class.getFields();
    for (Field field : fields) {
      int modifiers = field.getModifiers();
      if (Material.class.isAssignableFrom(field.getType())//
          && Modifier.isPublic(modifiers)//
          && Modifier.isStatic(modifiers)//
          && Modifier.isFinal(modifiers)//
      ) {
        Material material = (Material) field.get(null);
        String name = field.getName();
        expectedNames.put(material, name);
      }
    }

    // When:
    List<String> actual = expectedNames.keySet().stream() //
        .map(it -> MaterialClass.getName(it)) //
        .collect(Collectors.toList());

    // Then:
    assertThat(actual).containsExactlyElementsOf(expectedNames.values());
  }
}
