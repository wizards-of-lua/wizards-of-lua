package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagByte;

@AutoService(NbtFactory.class)
public class ByteNbtFactory extends AbstractNbtFactory<NBTTagByte> {
  @Override
  public String getNbtTypeName() {
    return "byte";
  }

  @Override
  public String getLuaTypeName() {
    return "boolean/number";
  }

  @Override
  public @Nullable NBTTagByte create(Object data, @Nullable NBTTagByte previousValue) {
    if (data instanceof Boolean) {
      data = (boolean) data ? 1 : 0;
    }
    if (data instanceof Number) {
      return new NBTTagByte(((Number) data).byteValue());
    }
    return null;
  }
}
