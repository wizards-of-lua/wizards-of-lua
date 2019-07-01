package net.wizardsoflua.lua.classes.scan;

import com.google.auto.service.AutoService;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
 * The <span class="notranslate">BlockHit</span> class contains the results of a call to the
 * entity's [scanView()](../Entity/#scanView) function.
 *
 */
@AutoService(LuaConverter.class)
@LuaClassAttributes(name = BlockHitClass.NAME)
@GenerateLuaClassTable(instance = BlockHitClass.Instance.class)
@GenerateLuaDoc(subtitle = "What is in Sight")
public final class BlockHitClass
    extends BasicLuaClass<RayTraceResult, BlockHitClass.Instance<RayTraceResult>> {
  public static final String NAME = "BlockHit";
  @Resource
  private LuaConverters converters;

  @Override
  protected Table createRawTable() {
    return new BlockHitClassTable<>(this, converters);
  }

  @Override
  protected Delegator<Instance<RayTraceResult>> toLuaInstance(RayTraceResult javaInstance) {
    return new BlockHitClassInstanceTable<>(new Instance<>(javaInstance), getTable(), converters);
  }

  @GenerateLuaInstanceTable
  public static class Instance<D extends RayTraceResult> extends LuaInstance<D> {
    public Instance(D delegate) {
      super(delegate);
    }

    /**
     * This is the exact position where the scan hit the block.
     */
    @LuaProperty
    public Vec3d getHitVec() {
      return delegate.hitVec;
    }

    /**
     * This is the position of the block that was hit by the scan.
     */
    @LuaProperty
    public BlockPos getPos() {
      return delegate.getBlockPos();
    }

    /**
     * This is the name of the block's side where the scan hit the block. This can be one of 'down',
     * 'up', 'south', 'west', 'north', and 'east'.
     */
    @LuaProperty
    public EnumFacing getSideHit() {
      return delegate.sideHit;
    }
  }
}
