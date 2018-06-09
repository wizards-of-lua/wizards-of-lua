package net.wizardsoflua.lua.converter;

import com.google.auto.service.AutoService;

import net.minecraft.nbt.NBTBase;
import net.wizardsoflua.extension.spell.spi.JavaToLuaConverter;

@AutoService(JavaToLuaConverter.class)
public class NbtConverter extends TypeTokenJavaToLuaConverter<NBTBase> {
  @Override
  public Object getLuaInstance(NBTBase javaInstance) {
    return net.wizardsoflua.lua.nbt.NbtConverter.toLua(javaInstance);
  }
}
