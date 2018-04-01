package net.wizardsoflua.lua.classes.block;

import static java.lang.reflect.Modifier.isFinal;
import static java.lang.reflect.Modifier.isPublic;
import static java.lang.reflect.Modifier.isStatic;

import java.lang.reflect.Field;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.ProxyingLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;

@DeclareLuaClass(name = MaterialClass.NAME)
public class MaterialClass
    extends InstanceCachingLuaClass<Material, MaterialClass.Proxy<Material>> {

  private static final Map<Material, String> NAMES = new IdentityHashMap<>();

  static {
    Field[] fields = Material.class.getFields();
    for (Field field : fields) {
      int modifiers = field.getModifiers();
      if (Material.class.isAssignableFrom(field.getType())//
          && isPublic(modifiers)//
          && isStatic(modifiers)//
          && isFinal(modifiers)//
      ) {
        try {
          Material material = (Material) field.get(null);
          String name = field.getName();
          NAMES.put(material, name);
        } catch (IllegalAccessException ex) {
          throw new UndeclaredThrowableException(ex);
        }
      }
    }
  }

  public static final String NAME = "Material";

  @Override
  public Proxy<Material> toLua(Material delegate) {
    return new Proxy<>(this, delegate);
  }

  public static class Proxy<D extends Material> extends LuaInstance<D> {
    public Proxy(ProxyingLuaClass<?, ?> luaClass, D delegate) {
      super(luaClass, delegate);
      addImmutable("blocksLight", delegate.blocksLight());
      addImmutable("blocksMovement", delegate.blocksMovement());
      addImmutable("canBurn", delegate.getCanBurn());
      addImmutable("liquid", delegate.isLiquid());
      addImmutable("mobility", getMobilityFlag());
      addImmutableNullable("name", getName());
      addImmutable("opaque", delegate.isOpaque());
      addImmutable("replaceable", delegate.isReplaceable());
      addImmutable("requiresNoTool", delegate.isToolNotRequired());
      addImmutable("solid", delegate.isSolid());
    }

    @Override
    public boolean isTransferable() {
      return true;
    }

    private Object getMobilityFlag() {
      return getConverters().toLua(delegate.getMobilityFlag());
    }

    private @Nullable String getName() {
      return NAMES.get(delegate);
    }
  }
}
