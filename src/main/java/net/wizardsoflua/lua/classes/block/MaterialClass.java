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

/**
 * The <span class="notranslate">Material</span> class describes the physical behaviour of a
 * [Block](modules/Block).
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = MaterialClass.NAME)
@GenerateLuaClassTable(instance = MaterialClass.Instance.class)
@GenerateLuaDoc(subtitle = "Physical Properties of Blocks")
public final class MaterialClass extends BasicLuaClass<Material, MaterialClass.Instance<Material>> {
  public static final String NAME = "Material";

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

    /**
     * This is <span class="notranslate">true</span> if entites can not pass this material.
     */
    @LuaProperty
    public boolean getBlocksMovement() {
      return delegate.blocksMovement();
    }

    /**
     * This is <span class="notranslate">true</span> if this material can catch fire.
     */
    @LuaProperty
    public boolean getCanBurn() {
      return delegate.isFlammable();
    }

    /**
     * This defines, if this material can be pushed, e.g. by a piston. The value is one of 'NORMAL',
     * 'DESTROY', 'BLOCK', 'IGNORE'.
     */
    @LuaProperty
    public EnumPushReaction getMobility() {
      return delegate.getPushReaction();
    }

    /**
     * This property contains the name of this material, if known, or nil, if not. This is something
     * like 'GRASS', 'WOOD', 'IRON', and many others.
     *
     * Please note that you must not confuse this with the [block name](/modules/Block/#name).
     *
     * For example, 'IRON' is the material not only of 'iron_bars', 'iron_block', 'iron_door',
     * 'iron_trapdoor', 'light_weighted_pressure_plate', and 'heavy_weighted_pressure_plate', but
     * also of 'gold_block', 'lapis_block', 'diamond_block', 'emerald_block', and 'redstone_block'.
     *
     */
    @LuaProperty
    public @Nullable String getName() {
      return MaterialClass.getName(delegate);
    }

    /**
     * This is <span class="notranslate">true</span> if this material can be harvested just by
     * hands.
     */
    @LuaProperty
    public boolean getRequiresNoTool() {
      return delegate.isToolNotRequired();
    }

    /**
     * This is <span class="notranslate">true</span> if this material is liquid and can flow.
     */
    @LuaProperty
    public boolean isLiquid() {
      return delegate.isLiquid();
    }

    /**
     * This is <span class="notranslate">true</span> if this material blocks the sight of entities.
     */
    @LuaProperty
    public boolean isOpaque() {
      return delegate.isOpaque();
    }

    /**
     * This is <span class="notranslate">true</span> if this material can be replaced by other
     * blocks, eg. snow, vines, and tall grass.
     */
    @LuaProperty
    public boolean isReplaceable() {
      return delegate.isReplaceable();
    }

    /**
     * This is <span class="notranslate">true</span> if this material is solid.
     */
    @LuaProperty
    public boolean isSolid() {
      return delegate.isSolid();
    }

    // TODO: Add color
  }
}
