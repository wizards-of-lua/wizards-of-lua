package net.wizardsoflua.lua.nbt.factory;

import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.nbt.NBTTagString;
import net.sandius.rembulan.ByteString;

@AutoService(NbtFactory.class)
public class StringNbtFactory extends SingleTypeNbtFactory<NBTTagString, ByteString> {
  @Override
  public NBTTagString createTypesafe(ByteString data, @Nullable NBTTagString previous) {
    return new NBTTagString(data.toString());
  }
}
