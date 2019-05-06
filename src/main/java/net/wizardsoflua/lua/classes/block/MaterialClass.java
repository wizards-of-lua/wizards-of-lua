package net.wizardsoflua.lua.classes.block;

import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.auto.service.AutoService;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.sandius.rembulan.Table;
import net.wizardsoflua.annotation.GenerateLuaClassTable;
import net.wizardsoflua.annotation.GenerateLuaDoc;
import net.wizardsoflua.annotation.GenerateLuaInstanceTable;
import net.wizardsoflua.annotation.LuaProperty;
import net.wizardsoflua.extension.api.inject.Resource;
import net.wizardsoflua.extension.spell.api.resource.LuaConverters;
import net.wizardsoflua.extension.spell.spi.LuaConverter;
import net.wizardsoflua.lua.classes.BasicLuaClass;
import net.wizardsoflua.lua.classes.LuaClassAttributes;
import net.wizardsoflua.lua.classes.LuaInstance;
import net.wizardsoflua.lua.classes.common.Delegator;

@AutoService(LuaConverter.class)
@LuaClassAttributes(name = MaterialClass.NAME)
@GenerateLuaClassTable(instance = MaterialClass.Instance.class)
@GenerateLuaDoc(subtitle = "Physical Properties of Blocks")
public final class MaterialClass extends BasicLuaClass<Material, MaterialClass.Instance<Material>> {
  public static final String NAME = "Material";

  private static final Map<Material, String> NAMES = new IdentityHashMap<>();
  static {
    NAMES.put(Material.AIR, "AIR");
    NAMES.put(Material.ANVIL, "ANVIL");
    NAMES.put(Material.BARRIER, "BARRIER");
    NAMES.put(Material.BUBBLE_COLUMN, "BUBBLE_COLUMN");
    NAMES.put(Material.CACTUS, "CACTUS");
    NAMES.put(Material.CAKE, "CAKE");
    NAMES.put(Material.CARPET, "CARPET");
    NAMES.put(Material.CIRCUITS, "CIRCUITS");
    NAMES.put(Material.CLAY, "CLAY");
    NAMES.put(Material.CLOTH, "CLOTH");
    NAMES.put(Material.CORAL, "CORAL");
    NAMES.put(Material.CRAFTED_SNOW, "CRAFTED_SNOW");
    NAMES.put(Material.DRAGON_EGG, "DRAGON_EGG");
    NAMES.put(Material.FIRE, "FIRE");
    NAMES.put(Material.GLASS, "GLASS");
    NAMES.put(Material.GOURD, "GOURD");
    NAMES.put(Material.GRASS, "GRASS");
    NAMES.put(Material.GROUND, "GROUND");
    NAMES.put(Material.ICE, "ICE");
    NAMES.put(Material.IRON, "IRON");
    NAMES.put(Material.LAVA, "LAVA");
    NAMES.put(Material.LEAVES, "LEAVES");
    NAMES.put(Material.OCEAN_PLANT, "OCEAN_PLANT");
    NAMES.put(Material.PACKED_ICE, "PACKED_ICE");
    NAMES.put(Material.PISTON, "PISTON");
    NAMES.put(Material.PLANTS, "PLANTS");
    NAMES.put(Material.PORTAL, "PORTAL");
    NAMES.put(Material.REDSTONE_LIGHT, "REDSTONE_LIGHT");
    NAMES.put(Material.ROCK, "ROCK");
    NAMES.put(Material.SAND, "SAND");
    NAMES.put(Material.SEA_GRASS, "SEA_GRASS");
    NAMES.put(Material.SNOW, "SNOW");
    NAMES.put(Material.SPONGE, "SPONGE");
    NAMES.put(Material.STRUCTURE_VOID, "STRUCTURE_VOID");
    NAMES.put(Material.TNT, "TNT");
    NAMES.put(Material.VINE, "VINE");
    NAMES.put(Material.WATER, "WATER");
    NAMES.put(Material.WEB, "WEB");
    NAMES.put(Material.WOOD, "WOOD");
  }

  public static @Nullable String getName(Material material) {
    return NAMES.get(material);
  }

  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new MaterialClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<Material>> toLuaInstance(Material javaInstance) {
    return new MaterialClassInstanceTable<>(new Instance<>(javaInstance), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends Material> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    @LuaProperty
    public boolean getBlocksMovement() {
      return delegate.blocksMovement();
    }

    @LuaProperty
    public boolean getCanBurn() {
      return delegate.isFlammable();
    }

    @LuaProperty
    public EnumPushReaction getMobility() {
      return delegate.getPushReaction();
    }

    @LuaProperty
    public @Nullable String getName() {
      return MaterialClass.getName(delegate);
    }

    @LuaProperty
    public boolean getRequiresNoTool() {
      return delegate.isToolNotRequired();
    }

    @LuaProperty
    public boolean isLiquid() {
      return delegate.isLiquid();
    }

    @LuaProperty
    public boolean isOpaque() {
      return delegate.isOpaque();
    }

    @LuaProperty
    public boolean isReplaceable() {
      return delegate.isReplaceable();
    }

    @LuaProperty
    public boolean isSolid() {
      return delegate.isSolid();
    }

    // TODO: Add color
  }
}
