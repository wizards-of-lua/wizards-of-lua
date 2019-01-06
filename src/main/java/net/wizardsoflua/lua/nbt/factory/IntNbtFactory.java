package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagInt;

@AutoService(NbtFactory.class)
public class IntNbtFactory extends SingleTypeNbtFactory<NBTTagInt, Number> {
  @Override
  public String getNbtTypeName() {
    return "int";
  }

  @Override
  public NBTTagInt createTypesafe(Number data, @Nullable NBTTagInt previous) {
    return new NBTTagInt(data.intValue());
  }
}
