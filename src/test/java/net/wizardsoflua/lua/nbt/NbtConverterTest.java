package net.wizardsoflua.lua.nbt;

import javax.annotation.Nullable;

import org.junit.Test;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.wizardsoflua.config.ConversionException;
import net.wizardsoflua.lua.classes.LuaClassLoader;
import net.wizardsoflua.lua.scheduling.LuaSchedulingContext;
import net.wizardsoflua.testenv.assertion.AssertionsFactory;

public class NbtConverterTest extends AssertionsFactory {
  private static final int NBT_STRING_TYPE = 8;

  private final NbtConverter underTest =
      new NbtConverter(new LuaClassLoader(new DefaultTable(), new LuaClassLoader.Context() {
        @Override
        public @Nullable LuaSchedulingContext getCurrentSchedulingContext() {
          return null;
        }
      }).getTypes());

  @Test
  public void test_merge__Uses_NbtType_of_existing_Tag() {
    // Given:
    NBTTagCompound nbt = new NBTTagCompound();
    String key = "my_key";
    short value = 42;
    nbt.setShort(key, value);

    Table data = new DefaultTable();
    long newValue = 53;
    data.rawset(key, newValue);

    // When:
    NBTTagCompound actual = underTest.merge(nbt, data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key);
    assertThat(actual.getTag(key)).isEqualTo(new NBTTagShort((short) newValue));
  }

  @Test
  public void test_merge__Can_create_new_Tag() {
    // Given:
    NBTTagCompound nbt = new NBTTagCompound();

    Table data = new DefaultTable();
    String key = "my_key";
    long value = 53;
    data.rawset(key, value);

    // When:
    NBTTagCompound actual = underTest.merge(nbt, data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key);
    assertThat(actual.getTag(key)).isEqualTo(new NBTTagLong(value));
  }

  @Test
  public void test_toNbtCompound__With_one_StringEntry() {
    // Given:
    Table data = new DefaultTable();
    String key = "my_key";
    String value = "my_value";
    data.rawset(key, value);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key);
    assertThat(actual.getTag(key)).isEqualTo(new NBTTagString(value));
  }

  @Test
  public void test_toNbtCompound__With_two_StringEntries() {
    // Given:
    Table data = new DefaultTable();
    String key1 = "my_key1";
    String value1 = "my_value1";
    data.rawset(key1, value1);
    String key2 = "my_key2";
    String value2 = "my_value2";
    data.rawset(key2, value2);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key1, key2);
    assertThat(actual.getTag(key1)).isEqualTo(new NBTTagString(value1));
    assertThat(actual.getTag(key2)).isEqualTo(new NBTTagString(value2));
  }

  @Test
  public void test_toNbtCompound__With_numeric_Key() {
    // Given:
    Table data = new DefaultTable();
    int key = 42;
    String value = "my_value";
    data.rawset(key, value);
    String keyString = String.valueOf(key);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(keyString);
    assertThat(actual.getTag(keyString)).isEqualTo(new NBTTagString(value));
  }

  @Test
  public void test_toNbtCompound__With_numeric_Value() {
    // Given:
    Table data = new DefaultTable();
    String key = "my_key";
    long value = 42;
    data.rawset(key, value);
    String keyString = String.valueOf(key);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(keyString);
    assertThat(actual.getTag(keyString)).isEqualTo(new NBTTagLong(value));
  }

  @Test
  public void test_toNbtCompound__With_List_Value() {
    // Given:
    Table data = new DefaultTable();
    String key = "my_key";
    Table value = new DefaultTable();
    data.rawset(key, value);
    long key2 = 1;
    String value2 = "my_value2";
    value.rawset(key2, value2);
    long key3 = 2;
    String value3 = "my_value3";
    value.rawset(key3, value3);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key);
    assertThat(actual.getTag(key)).isExactlyInstanceOf(NBTTagList.class);
    NBTTagList actualValue = actual.getTagList(key, NBT_STRING_TYPE);
    assertThat(actualValue).containsExactly(new NBTTagString(value2), new NBTTagString(value3));
  }

  @Test
  public void test_toNbtCompound__With_Compound_Value() {
    // Given:
    Table data = new DefaultTable();
    String key = "my_key";
    Table value = new DefaultTable();
    data.rawset(key, value);
    String key2 = "my_key2";
    long value2 = 42;
    value.rawset(key2, value2);

    // When:
    NBTTagCompound actual = underTest.toNbtCompound(data);

    // Then:
    assertThat(actual.getKeySet()).containsOnly(key);
    assertThat(actual.getTag(key)).isExactlyInstanceOf(NBTTagCompound.class);
    NBTTagCompound actualValue = actual.getCompoundTag(key);
    assertThat(actualValue.getKeySet()).containsOnly(key2);
    assertThat(actualValue.getTag(key2)).isEqualTo(new NBTTagLong(value2));
  }

  @Test
  public void test_toNbtCompound__With_Table_Key_throws_Exception() {
    // Given:
    Table data = new DefaultTable();
    Table key = new DefaultTable();
    String value = "my_value";
    data.rawset(key, value);

    // When:
    ConversionException actual = null;
    try {
      underTest.toNbtCompound(data);
    } catch (ConversionException ex) {
      actual = ex;
    }

    // Then:
    assertThat(actual)
        .hasMessage("Can't convert key 1 in nbt! string/number expected, but got table");
  }

  @Test
  public void test_toNbtCompound__With_nested_Table_Key_throws_Exception() {
    // Given:
    Table data = new DefaultTable();
    String key1 = "my_key";
    Table value1 = new DefaultTable();
    data.rawset(key1, value1);
    Table key2 = new DefaultTable();
    String value2 = "my_value";
    value1.rawset(key2, value2);

    // When:
    ConversionException actual = null;
    try {
      underTest.toNbtCompound(data);
    } catch (ConversionException ex) {
      actual = ex;
    }

    // Then:
    assertThat(actual)
        .hasMessage("Can't convert key 1 in nbt.my_key! string/number expected, but got table");
  }

}
