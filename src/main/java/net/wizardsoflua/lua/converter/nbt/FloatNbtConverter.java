package net.wizardsoflua.lua.converter.nbt;

import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagFloat;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;
import net.wizardsoflua.lua.converter.AnnotatedJavaToLuaConverter;
import net.wizardsoflua.lua.converter.LuaConverterAttributes;
import net.wizardsoflua.lua.nbt.NbtConverter;

@AutoService(JavaToLuaConverter.class)
@LuaConverterAttributes(name = "table")
public class FloatNbtConverter extends AnnotatedJavaToLuaConverter<NBTTagFloat> {
  @Override
  public Object getLuaInstance(NBTTagFloat javaInstance) {
    return NbtConverter.toLua(javaInstance);
  }
}
