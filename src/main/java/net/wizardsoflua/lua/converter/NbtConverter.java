package net.wizardsoflua.lua.converter;

import com.google.auto.service.AutoService;
import net.minecraft.nbt.INBTBase;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;

@AutoService(JavaToLuaConverter.class)
@LuaConverterAttributes(name = "table")
public class NbtConverter extends AnnotatedJavaToLuaConverter<INBTBase> {
  @Override
  public Object getLuaInstance(INBTBase javaInstance) {
    return net.wizardsoflua.lua.nbt.NbtConverter.toLua(javaInstance);
  }
}
