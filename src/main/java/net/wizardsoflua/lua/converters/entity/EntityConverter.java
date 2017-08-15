package net.wizardsoflua.lua.converters.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.Table;
import net.wizardsoflua.lua.converters.Converters;
import net.wizardsoflua.lua.converters.common.DelegatingProxy;

public class EntityConverter {
  public static final String METATABLE_NAME = "Entity";

  private final Converters converters;
  private final Table metatable;

  public EntityConverter(Converters converters) {
    this.converters = converters;
    // TODO do declaration outside this class
    this.metatable = converters.getTypes().declare(METATABLE_NAME);
  }

  public Table toLua(Entity delegate) {
    return new Proxy(converters, metatable, delegate);
  }

  public static class Proxy extends DelegatingProxy {

    private final Entity delegate;

    public Proxy(Converters converters, Table metatable, Entity delegate) {
      super(converters, metatable, delegate);
      this.delegate = delegate;
      addReadOnly("dimension", () -> delegate.dimension);
      addReadOnly("uuid", this::getUuid);
      add("name", this::getName, this::setName);
      add("pos", this::getPos, this::setPos);
    }

    public Table getPos() {
      return getConverters().vec3ToLua(delegate.getPositionVector());
    }

    public void setPos(Object luaObj) {
      Vec3d pos = getConverters().vec3ToJava(luaObj);
      delegate.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
    }

    public ByteString getUuid() {
      return getConverters().stringToLua(delegate.getUniqueID().toString());
    }

    public ByteString getName() {
      return getConverters().stringToLua(delegate.getName());
    }

    public void setName(Object luaObj) {
      String name = getConverters().stringToJava(luaObj);
      delegate.setCustomNameTag(name);
    }

  }

}
