package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.impl.DefaultTable;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.Converters;
import net.wizardsoflua.lua.function.NamedFunction2;

public class ItemsModule {
  public static ItemsModule installInto(Table env, Converters converters) {
    ItemsModule result = new ItemsModule(converters);
    env.rawset("Items", result.getLuaTable());
    return result;
  }

  private final Converters converters;
  private final Table luaTable = DefaultTable.factory().newTable();

  public ItemsModule(Converters converters) {
    this.converters = converters;
    GetFunction getFunction = new GetFunction();
    luaTable.rawset(getFunction.getName(), getFunction);
  }

  public Table getLuaTable() {
    return luaTable;
  }

  public @Nullable Object get(String itemId, int amount) {
    Item item = Item.getByNameOrId(itemId);
    ItemStack result = new ItemStack(item, amount);
    return converters.toLuaNullable(result);
  }

  private class GetFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "get";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String itemId = converters.toJava(String.class, arg1, 1, "itemId", getName());
      int amount = converters.toJavaOptional(Integer.class, arg2, 2, "amount", getName()).orElse(1);
      Object result = get(itemId, amount);
      context.getReturnBuffer().setTo(result);
    }
  }
}
