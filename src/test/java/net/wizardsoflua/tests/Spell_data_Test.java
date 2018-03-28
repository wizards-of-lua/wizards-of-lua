package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;

@RunWith(MinecraftJUnitRunner.class)
public class Spell_data_Test extends WolTestBase {

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_Spell_can_read_and_write_its_own_Specifics
  // @formatter:on
  @Test
  public void test_Spell_can_read_and_write_its_own_Specifics() {
    // When:
    mc().executeCommand("lua spell.data.blub = 'bla'\n"//
        + "print(spell.data.blub)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("bla");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_Spell_can_read_and_write_Specifics_of_other_Spell
  // @formatter:on
  @Test
  public void test_Spell_can_read_and_write_Specifics_of_other_Spell() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "other.data.blub = 'bla'\n"//
        + "print(other.data.blub)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("bla");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_other_Spell_can_read_its_own_Specifics_when_written_by_different_Spell
  // @formatter:on
  @Test
  public void test_other_Spell_can_read_its_own_Specifics_when_written_by_different_Spell() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "sleep(2)\n"//
        + "print(spell.data.blub)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "other.data.blub = 'bla'\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("bla");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_Spell_can_read_Specifics_of_other_Spell_when_written_by_other_Spell
  // @formatter:on
  @Test
  public void test_Spell_can_read_Specifics_of_other_Spell_when_written_by_other_Spell() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "spell.data.blub = 'bla'\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "print(other.data.blub)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("bla");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_data_can_contain_a_Table_that_contains_itself
  // @formatter:on
  @Test
  public void test_data_can_contain_a_Table_that_contains_itself() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "local data = {}\n"//
        + "data.bla = data\n"//
        + "spell.data.blub = data\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "local data = other.data.blub\n"//
        + "print(data == data.bla)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_if_Dataclass_is_known_to_both_Classloaders_its_replaced_by_the_corresponding_Class
  // @formatter:on
  @Test
  public void test_if_Dataclass_is_known_to_both_Classloaders_its_replaced_by_the_corresponding_Class() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "declare 'MyClass'\n"//
        + "local data = {}\n"//
        + "setmetatable(data, MyClass)\n"//
        + "spell.data.blub = data\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "declare 'MyClass'\n"//
        + "local data = other.data.blub\n"//
        + "local mt = getmetatable(data)\n"//
        + "print(mt == MyClass)\n"//
        + "print(str(mt))\n"//
        + "print(str(MyClass))\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_if_Dataclass_is_not_known_to_target_Classloader_the_Metatable_is_nil
  // @formatter:on
  @Test
  public void test_if_Dataclass_is_not_known_to_target_Classloader_the_Metatable_is_nil() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "declare 'MyClass'\n"//
        + "local data = {}\n"//
        + "setmetatable(data, MyClass)\n"//
        + "spell.data.blub = data\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "local data = other.data.blub\n"//
        + "local mt = getmetatable(data)\n"//
        + "print(mt)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("nil");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_if_Data_is_a_Proxy_it_will_be_a_Proxy_in_the_target_Classloader
  // @formatter:on
  @Test
  public void test_if_Data_is_a_Proxy_it_will_be_a_Proxy_in_the_target_Classloader() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "spell.data.blub = spell\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "print(other.data.blub == other)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_if_Data_contains_a_Proxy_it_will_be_a_Proxy_in_the_target_Classloader
  // @formatter:on
  @Test
  public void test_if_Data_contains_a_Proxy_it_will_be_a_Proxy_in_the_target_Classloader() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "spell.data.blub = {bla = spell}\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "print(other.data.blub.bla == other)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_The_data_of_another_Spell_is_always_the_same_Object
  // @formatter:on
  @Test
  public void test_The_data_of_another_Spell_is_always_the_same_Object() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "print(other.data == other.data)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_if_the_same_Table_is_added_and_accessed_multiple_times_it_stays_the_same_Table
  // @formatter:on
  @Test
  public void test_if_the_same_Table_is_added_and_accessed_multiple_times_it_stays_the_same_Table() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "local otherTable = {data = 'ab'}\n"//
        + "spell.data.a = otherTable\n"//
        + "spell.data.b = otherTable\n"//
        + "print('other spell a==otherTable: '..tostring(spell.data.a == otherTable))\n"//
        + "print('other spell a==b: '..tostring(spell.data.a == spell.data.b))\n"//
        + "sleep(2)\n"//
        + "print('other spell c==d: '..tostring(spell.data.c == spell.data.d))\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "print('main spell a==b: '..tostring(other.data.a == other.data.b))\n"//
        + "local aTable = {data = 'cd'}\n"//
        + "other.data.c = aTable\n"//
        + "other.data.d = aTable\n"//
        + "print('main spell c==aTable: '..tostring(other.data.c == aTable))\n"//
        + "print('main spell c==d: '..tostring(other.data.c == other.data.d))\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("other spell a==otherTable: true");
    assertThat(mc().nextServerMessage()).isEqualTo("other spell a==b: true");
    assertThat(mc().nextServerMessage()).isEqualTo("main spell a==b: true");
    assertThat(mc().nextServerMessage()).isEqualTo("main spell c==aTable: true");
    assertThat(mc().nextServerMessage()).isEqualTo("main spell c==d: true");
    assertThat(mc().nextServerMessage()).isEqualTo("other spell c==d: true");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_Calling_a_function_from_a_different_Spell_is_not_allowed
  // @formatter:on
  @Test
  public void test_Calling_a_function_from_a_different_Spell_is_not_allowed() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "spell.data.func = function() print('executed func') end\n"//
        + "print('other')\n"//
        + "spell.data.func()\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "local func = other.data.func\n"//
        + "print(type(func))\n"//
        + "func()\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("other");
    assertThat(mc().nextServerMessage()).isEqualTo("executed func");
    assertThat(mc().nextServerMessage()).isEqualTo("function");
    assertThat(mc().nextServerMessage())
        .contains("attempt to call a function from a different spell");
  }

  // @formatter:off
  // /test net.wizardsoflua.tests.Spell_data_Test test_Setting_the_Metatable_of_a_Table_from_a_different_Spell_is_not_allowed
  // @formatter:on
  @Test
  public void test_Setting_the_Metatable_of_a_Table_from_a_different_Spell_is_not_allowed() {
    // When:
    mc().executeCommand("lua spell.name = 'other'\n"//
        + "spell.data.tbl = {}\n"//
        + "local mt = {}\n"//
        + "print('other')\n"//
        + "setmetatable(spell.data.tbl, mt)\n"//
        + "sleep(2)\n"//
    );
    mc().executeCommand("lua local other = Entities.find('@e[type=wol:spell,name=other]')[1]\n"//
        + "local tbl = other.data.tbl\n"//
        + "local mt = {}\n"//
        + "print('main')\n"//
        + "setmetatable(tbl, mt)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo("other");
    assertThat(mc().nextServerMessage()).isEqualTo("main");
    assertThat(mc().nextServerMessage())
        .contains("attempt to set the metatable of a table from a different spell");
  }

}
