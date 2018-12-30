package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagShort;

@AutoService(NbtFactory.class)
public class ShortNbtFactory extends SingleTypeNbtFactory<NBTTagShort, Number> {
  @Override
  public NBTTagShort createTypesafe(Number data, @Nullable NBTTagShort previous) {
    return new NBTTagShort(data.shortValue());
  }
}
