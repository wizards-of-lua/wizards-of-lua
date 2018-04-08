package net.wizardsoflua.lua.module.items;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.wizardsoflua.lua.extension.api.Converter;
import net.wizardsoflua.lua.extension.api.InitializationContext;
import net.wizardsoflua.lua.extension.api.function.NamedFunction2;
import net.wizardsoflua.lua.extension.spi.LuaExtension;
import net.wizardsoflua.lua.extension.util.AbstractLuaModule;

@AutoService(LuaExtension.class)
public class ItemsModule extends AbstractLuaModule {
  private Table table;
  private Converter converter;

  @Override
  public void initialize(InitializationContext context) {
    table = context.getTableFactory().newTable();
    converter = context.getConverter();
    add(new GetFunction());
  }

  @Override
  public String getName() {
    return "Items";
  }

  @Override
  public Table getLuaObject() {
    return table;
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
