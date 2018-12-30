package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagLong;

@AutoService(NbtFactory.class)
public class LongNbtFactory extends SingleTypeNbtFactory<NBTTagLong, Number> {
  @Override
  public NBTTagLong createTypesafe(Number data, @Nullable NBTTagLong previous) {
    return new NBTTagLong(data.longValue());
  }
}
