package net.wizardsoflua.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import net.wizardsoflua.nbt.NbtUtils;
import net.wizardsoflua.testenv.MinecraftJUnitRunner;
import net.wizardsoflua.testenv.WolTestBase;

@RunWith(MinecraftJUnitRunner.class)
public class ItemNbtTest extends WolTestBase {

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_Array_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Byte_Array_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "{ 5, 6, 7 }";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte_array', value = " + expected + " }\n"//
        + "print(str(item.nbt.tag.abc))\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagByteArray.class);
    assertThat(((NBTTagByteArray) actual).getByteArray()).containsExactly((byte) 5, (byte) 6,
        (byte) 7);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_List_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Byte_List_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "{ 5, 6, 7 }";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte_list', value = " + expected + " }\n"//
        + "print(str(item.nbt.tag.abc))\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagList.class);
    NBTTagList list = (NBTTagList) actual;
    assertThat(list.getTagType()).isEqualTo(NbtUtils.BYTE);
    assertThat(list).containsExactly(new NBTTagByte((byte) 5), new NBTTagByte((byte) 6),
        new NBTTagByte((byte) 7));
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_List_NBT_with_inconvertable_Element
  @Test
  public void test_creating_Byte_List_NBT_with_inconvertable_Element() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte', value = { 'bla' } }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to byte NBT: expected boolean/number but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_List_NBT_with_inconvertable_Value
  @Test
  public void test_creating_Byte_List_NBT_with_inconvertable_Value() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte_list', value = 'bla' }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to list NBT: expected table but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Byte_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    byte expected = 5;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagByte.class);
    assertThat(((NBTTagByte) actual).getByte()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Byte_NBT_with_inconvertable_Value
  @Test
  public void test_creating_Byte_NBT_with_inconvertable_Value() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte', value = 'bla' }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to byte NBT: expected boolean/number but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Double_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Double_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    double expected = 5.25;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'double', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagDouble.class);
    assertThat(((NBTTagDouble) actual).getDouble()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest
  // test_creating_empty_Byte_List_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_empty_Byte_List_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "{}";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte_list', value = " + expected + " }\n"//
        + "print(str(item.nbt.tag.abc))\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagList.class);
    NBTTagList list = (NBTTagList) actual;
    assertThat(list.getTagType()).isEqualTo(NbtUtils.BYTE);
    assertThat(list).isEmpty();
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Float_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Float_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    float expected = 5.25f;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'float', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagFloat.class);
    assertThat(((NBTTagFloat) actual).getFloat()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_Array_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Int_Array_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "{ 5, 6, 7 }";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int_array', value = " + expected + " }\n"//
        + "print(str(item.nbt.tag.abc))\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagIntArray.class);
    assertThat(((NBTTagIntArray) actual).getIntArray()).containsExactly(5, 6, 7);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_List_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Int_List_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "{ 5, 6, 7 }";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int_list', value = " + expected + " }\n"//
        + "print(str(item.nbt.tag.abc))\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagList.class);
    NBTTagList list = (NBTTagList) actual;
    assertThat(list.getTagType()).isEqualTo(NbtUtils.INT);
    assertThat(list).containsExactly(new NBTTagInt(5), new NBTTagInt(6), new NBTTagInt(7));
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_List_NBT_with_inconvertable_Element
  @Test
  public void test_creating_Int_List_NBT_with_inconvertable_Element() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int', value = { 'bla' } }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to int NBT: expected number but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_List_NBT_with_inconvertable_Value
  @Test
  public void test_creating_Int_List_NBT_with_inconvertable_Value() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int_list', value = 'bla' }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to list NBT: expected table but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Int_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    int expected = 5;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagInt.class);
    assertThat(((NBTTagInt) actual).getInt()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Int_NBT_with_inconvertable_Value
  @Test
  public void test_creating_Int_NBT_with_inconvertable_Value() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'int', value = 'bla' }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).startsWith(
        "Error during spell execution: Cannot convert 'bla' to int NBT: expected number but got string");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Long_Array_NBT_is_not_supported
  /**
   * We don't support long array NBTs, because the class {@link NBTTagLongArray} does not offer a
   * getter for the underlying array.
   */
  @Test
  public void test_creating_Long_Array_NBT_is_not_supported() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'long_array', value = { 5, 6, 7 } }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage())
        .startsWith("Error during spell execution: Unknown nbt type 'long_array'");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Long_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Long_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    long expected = 5;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'long', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagLong.class);
    assertThat(((NBTTagLong) actual).getLong()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_Short_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_Short_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    short expected = 5;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'short', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagShort.class);
    assertThat(((NBTTagShort) actual).getShort()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_creating_String_NBT_by_specifying_NBT_Type
  @Test
  public void test_creating_String_NBT_by_specifying_NBT_Type() throws Exception {
    // Given:
    String expected = "bla";

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'string', value = '" + expected + "' }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo(expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagString.class);
    assertThat(((NBTTagString) actual).getString()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_NBT_Type_can_be_changed_by_specifying_it
  @Test
  public void test_NBT_Type_can_be_changed_by_specifying_it() throws Exception {
    // Given:
    short expected = 5;

    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte', value = " + expected + " }\n"//
        + "item.nbt.tag.abc = { __nbttype = 'short', value = " + expected + " }\n"//
        + "print(item.nbt.tag.abc)\n"//
        + "spell.owner.mainhand = item\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage()).isEqualTo("" + expected);

    NBTBase actual = mc().player().getMainHandItem().getTagCompound().getTag("abc");
    assertThat(actual).isExactlyInstanceOf(NBTTagShort.class);
    assertThat(((NBTTagShort) actual).getShort()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_NBT_Type_cannot_be_specified_without_Value
  @Test
  public void test_NBT_Type_cannot_be_specified_without_Value() throws Exception {
    // When:
    mc().player().chat("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = {}\n"//
        + "item.nbt.tag.abc = { __nbttype = 'byte' }\n"//
    );

    // Then:
    assertThat(mc().nextClientMessage())
        .startsWith("Error during spell execution: missing NBT value");
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_String_NBT_is_readable
  @Test
  public void test_String_NBT_is_readable() throws Exception {
    // Given:
    String expected = "bla";

    // When:
    mc().executeCommand("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = { title = '" + expected + "'}\n"//
        + "print(item.nbt.tag.title)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

  // /test net.wizardsoflua.tests.ItemNbtTest test_String_NBT_is_writable
  @Test
  public void test_String_NBT_is_writable() throws Exception {
    // Given:
    String expected = "bla";

    // When:
    mc().executeCommand("/lua local item = Items.get('written_book')\n"//
        + "item.nbt.tag = { title = 'bli' }\n"//
        + "item.nbt.tag.title = '" + expected + "'\n"//
        + "print(item.nbt.tag.title)\n"//
    );

    // Then:
    assertThat(mc().nextServerMessage()).isEqualTo(expected);
  }

}
