package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagFloat;

@AutoService(NbtFactory.class)
public class FloatNbtFactory extends SingleTypeNbtFactory<NBTTagFloat, Number> {
  @Override
  public NBTTagFloat createTypesafe(Number data, @Nullable NBTTagFloat previous) {
    return new NBTTagFloat(data.floatValue());
  }
}
