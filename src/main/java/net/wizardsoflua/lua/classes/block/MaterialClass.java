package net.wizardsoflua.lua.classes.block;

import java.util.IdentityHashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.block.material.Material;
import net.wizardsoflua.lua.classes.DeclareLuaClass;
import net.wizardsoflua.lua.classes.InstanceCachingLuaClass;
import net.wizardsoflua.lua.classes.DelegatorLuaClass;
import net.wizardsoflua.lua.classes.common.LuaInstance;

@DeclareLuaClass(name = MaterialClass.NAME)
public class MaterialClass
    extends InstanceCachingLuaClass<Material, MaterialClass.Proxy<Material>> {
  private static final Map<Material, String> NAMES = new IdentityHashMap<>();

  static {
    NAMES.put(Material.AIR, "AIR");
    NAMES.put(Material.GRASS, "GRASS");
    NAMES.put(Material.GROUND, "GROUND");
    NAMES.put(Material.WOOD, "WOOD");
    NAMES.put(Material.ROCK, "ROCK");
    NAMES.put(Material.IRON, "IRON");
    NAMES.put(Material.ANVIL, "ANVIL");
    NAMES.put(Material.WATER, "WATER");
    NAMES.put(Material.LAVA, "LAVA");
    NAMES.put(Material.LEAVES, "LEAVES");
    NAMES.put(Material.PLANTS, "PLANTS");
    NAMES.put(Material.VINE, "VINE");
    NAMES.put(Material.SPONGE, "SPONGE");
    NAMES.put(Material.CLOTH, "CLOTH");
    NAMES.put(Material.FIRE, "FIRE");
    NAMES.put(Material.SAND, "SAND");
    NAMES.put(Material.CIRCUITS, "CIRCUITS");
    NAMES.put(Material.CARPET, "CARPET");
    NAMES.put(Material.GLASS, "GLASS");
    NAMES.put(Material.REDSTONE_LIGHT, "REDSTONE_LIGHT");
    NAMES.put(Material.TNT, "TNT");
    NAMES.put(Material.CORAL, "CORAL");
    NAMES.put(Material.ICE, "ICE");
    NAMES.put(Material.PACKED_ICE, "PACKED_ICE");
    NAMES.put(Material.SNOW, "SNOW");
    NAMES.put(Material.CRAFTED_SNOW, "CRAFTED_SNOW");
    NAMES.put(Material.CACTUS, "CACTUS");
    NAMES.put(Material.CLAY, "CLAY");
    NAMES.put(Material.GOURD, "GOURD");
    NAMES.put(Material.DRAGON_EGG, "DRAGON_EGG");
    NAMES.put(Material.PORTAL, "PORTAL");
    NAMES.put(Material.CAKE, "CAKE");
    NAMES.put(Material.WEB, "WEB");
    NAMES.put(Material.PISTON, "PISTON");
    NAMES.put(Material.BARRIER, "BARRIER");
    NAMES.put(Material.STRUCTURE_VOID, "STRUCTURE_VOID");
  }

  public static String GET_NAME(Material material) {
    return NAMES.get(material);
  }

  public static final String NAME = "Material";

  @Override
  public Proxy<Material> toLua(Material delegate) {
    return new Proxy<>(this, delegate);
  }

  public static class Proxy<D extends Material> extends LuaInstance<D> {
    public Proxy(DelegatorLuaClass<?, ?> luaClass, D delegate) {
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
      return getConverter().toLua(delegate.getMobilityFlag());
    }

    private @Nullable String getName() {
      return GET_NAME(delegate);
    }
  }
}
