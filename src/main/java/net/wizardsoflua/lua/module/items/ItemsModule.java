package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.TableFactory;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.inject.Inject;
import net.wizardsoflua.lua.extension.api.service.Converter;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;
import net.wizardsoflua.lua.function.NamedFunction2;

@AutoService(LuaExtension.class)
public class ItemsModule extends AbstractLuaModule {
  @Inject
  private Converter converter;
  @Inject
  private TableFactory tableFactory;

  public ItemsModule() {
    add(new GetFunction());
  }

  @Override
  public String getName() {
    return "Items";
  }

  @Override
  public Table getTable() {
    return tableFactory.newTable();
  }

  public Object get(String name, @Nullable Integer amount) {
    amount = amount != null ? amount : 1;
    Item item = Item.getByNameOrId(name);
    return new ItemStack(item, amount);
  }

  private class GetFunction extends NamedFunction2 {
    @Override
    public String getName() {
      return "get";
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1, Object arg2)
        throws ResolvedControlThrowable {
      String name = converter.toJava(String.class, arg1, 1, "name", getName());
      Integer amount = converter.toJavaNullable(Integer.class, arg2, 2, "amount", getName());
      Object result = get(name, amount);
      Object luaResult = converter.toLua(result);
      context.getReturnBuffer().setTo(luaResult);
    }
  }
}
