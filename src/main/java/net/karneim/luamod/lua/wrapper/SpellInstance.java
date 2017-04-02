package net.karneim.luamod.lua.wrapper;

import static net.karneim.luamod.lua.util.PreconditionsUtils.checkType;

import javax.annotation.Nullable;

import net.karneim.luamod.cursor.Spell;
import net.karneim.luamod.lua.classes.LuaTypesRepo;
import net.karneim.luamod.lua.util.table.DelegatingTable;
import net.karneim.luamod.lua.util.wrapper.DelegatingTableWrapper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.Vec3d;
import net.sandius.rembulan.Table;

// TODO extend from EntityInstance and add it to EntityWrapperFactory
public class SpellInstance extends DelegatingTableWrapper<Spell> {

  public SpellInstance(LuaTypesRepo repo, @Nullable Spell delegate, Table metatable) {
    super(repo, delegate, metatable);
  }

  @Override
  protected void addProperties(DelegatingTable.Builder b) {
    b.add("block", () -> WrapperFactory.wrap(getRepo(), delegate.getBlockState()), this::setBlock);
    b.add("orientation", () -> WrapperFactory.wrap(getRepo(), delegate.getOrientation()),
        this::setOrientation);
    b.add("origin", () -> WrapperFactory.wrap(getRepo(), delegate.getOrigin()), null);
    b.add("owner", () -> WrapperFactory.wrap(getRepo(), delegate.getOwner()), null);
    // b.add("rotation", () -> WrapperFactory.wrap(getRepo(), delegate.getRotation()),
    // this::setRotation);
    b.add("rotation", () -> delegate.getRotation(), this::setRotation);
    b.add("surface", () -> WrapperFactory.wrap(getRepo(), delegate.getSurface()), null);
    b.add("pos", () -> WrapperFactory.wrap(getRepo(), delegate.getWorldPosition()),
        this::setPosition);
  }

  private void setPosition(Object arg) {
    Table vector = checkType(arg, Table.class);
    Number x = checkType(vector.rawget("x"), Number.class);
    Number y = checkType(vector.rawget("y"), Number.class);
    Number z = checkType(vector.rawget("z"), Number.class);
    Vec3d v = new Vec3d(x.doubleValue(), y.doubleValue(), z.doubleValue());
    delegate.setWorldPosition(v);
  }

  private void setRotation(Object arg) {
    if (arg instanceof Number) {
      float angle = ((Number) arg).floatValue();
      delegate.setRotation(angle);
    } else {
      Rotation rot = Rotation.valueOf(String.valueOf(arg));
      if (rot != null) {
        delegate.setRotation(rot);
      } else {
        throw new IllegalArgumentException(
            String.format("Rotation value expected but got %s!", arg));
      }
    }
  }

  private void setOrientation(Object arg) {
    EnumFacing f = EnumFacing.byName(String.valueOf(arg));
    if (f != null) {
      delegate.setOrientation(f);
    } else {
      throw new IllegalArgumentException(String.format("Facing value expected but got %s!", arg));
    }
  }

  private void setBlock(Object arg) {
    if (arg == null) {
      throw new IllegalArgumentException(String.format("Block value expected but got nil!"));
    }
    try {
      Block block = null;
      if (arg instanceof DelegatingTable) {
        DelegatingTable table = (DelegatingTable) arg;
        String typename = String.valueOf(table.rawget("type"));
        if ("Block".equals(typename)) {
          IBlockState blockState = (IBlockState) table.getDelegate();
          delegate.setBlockState(blockState);
          return;
        } else {
          throw new IllegalArgumentException(
              String.format("Block value expected but got %s!", arg));
        }
      } else if (arg instanceof Table) {
        Table table = (Table) arg;
        String typename = String.valueOf(table.rawget("type"));
        String name = String.valueOf(table.rawget("name"));
        if ("Block".equals(typename)) {
          block = delegate.getBlockByName(name);
        }
      } else {
        block = delegate.getBlockByName(String.valueOf(arg));
      }
      if (block != null) {
        delegate.setBlock(block);
      } else {
        throw new IllegalArgumentException(String.format("Block value expected but got %s!", arg));
      }
    } catch (NumberInvalidException e) {
      throw new IllegalArgumentException(
          String.format("Block value expected but received exception: %s!", e.getMessage()));
    }
  }

}
