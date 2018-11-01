package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagByte;

@AutoService(NbtFactory.class)
public class ByteNbtFactory extends AbstractNbtFactory<NBTTagByte, Number> {
  @Override
  public NBTTagByte create(Number data, @Nullable NBTTagByte previous) {
    return new NBTTagByte(data.byteValue());
  }
}
