package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagDouble;

@AutoService(NbtFactory.class)
public class DoubleNbtFactory extends AbstractNbtFactory<NBTTagDouble, Number> {
  @Override
  public NBTTagDouble create(Number data, @Nullable NBTTagDouble previous) {
    return new NBTTagDouble(data.doubleValue());
  }
}
