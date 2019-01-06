package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagDouble;

@AutoService(NbtFactory.class)
public class DoubleNbtFactory extends SingleTypeNbtFactory<NBTTagDouble, Number> {
  @Override
  public String getNbtTypeName() {
    return "double";
  }

  @Override
  public NBTTagDouble createTypesafe(Number data, @Nullable NBTTagDouble previous) {
    return new NBTTagDouble(data.doubleValue());
  }
}
