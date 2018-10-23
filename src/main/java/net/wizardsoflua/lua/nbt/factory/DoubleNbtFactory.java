package net.wizardsoflua.lua.nbt.factory;

import com.google.auto.service.AutoService;

import net.minecraft.nbt.NBTTagDouble;

@AutoService(NbtFactory.class)
public class DoubleNbtFactory extends AbstractNbtFactory<NBTTagDouble, Number> {
  @Override
  public NBTTagDouble create(Number data) {
    return new NBTTagDouble(data.doubleValue());
  }
}
