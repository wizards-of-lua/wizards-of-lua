package net.wizardsoflua.lua.nbt.factory;

import com.google.auto.service.AutoService;

import net.minecraft.nbt.NBTTagByte;

@AutoService(NbtFactory.class)
public class ByteNbtFactory extends AbstractNbtFactory<NBTTagByte, Number> {
  @Override
  public NBTTagByte create(Number data) {
    return new NBTTagByte(data.byteValue());
  }
}
