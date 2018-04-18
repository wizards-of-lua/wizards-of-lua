package net.wizardsoflua.lua.view;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nullable;

import com.google.auto.service.AutoService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.wizardsoflua.lua.classes.JavaInstanceWrapper;
import net.wizardsoflua.lua.classes.common.LuaInstance;
import net.wizardsoflua.lua.extension.api.inject.AfterInjection;
import net.wizardsoflua.lua.extension.api.inject.Resource;
import net.wizardsoflua.lua.extension.api.service.LuaConverters;
import net.wizardsoflua.lua.extension.api.service.SpellExtensions;
import net.wizardsoflua.lua.extension.spi.SpellExtension;
import net.wizardsoflua.lua.module.types.TypesModule;

/**
 * A factory that can create {@link View}s of lua objects from different spells. These views are
 * necessary, because we want to prevent wizards from calling functions that were defined in a
 * different spell.
 *
 * @author Adrodoc
 * @see FunctionView#invoke(ExecutionContext)
 */
@AutoService(SpellExtension.class)
public class ViewFactory implements SpellExtension {
  @Resource
  private LuaConverters converters;
  @Resource
  private SpellExtensions extensions;

  private final Cache<Object, View> cache =
      CacheBuilder.newBuilder().weakKeys().softValues().build();
  private TypesModule types;

  @AfterInjection
  public void init() {
    types = extensions.getSpellExtension(TypesModule.class);
  }

  public @Nullable String getClassName(Table table) {
    return types.getClassName(table);
  }

  public @Nullable Table getClassTableForName(String className) {
    return types.getClassTableForName(className);
  }

  /**
   * Returns a new or cached {@link View} of {@code remoteLuaObject}. If {@code remoteLuaObject} is
   * {@code null} then returns {@code null}. If {@code remoteLuaObject} is a {@link View} of a local
   * lua object then the local lua object is returned.
   *
   * @param remoteLuaObject
   * @param remoteViewFactory
   * @return a view of {@code remoteLuaObject}
   */
  public @Nullable Object getView(@Nullable Object remoteLuaObject, ViewFactory remoteViewFactory) {
    checkArgument(remoteViewFactory != this, "Trying to create remote view of local object");
    if (remoteLuaObject instanceof View) {
      View remoteView = (View) remoteLuaObject;
      remoteLuaObject = remoteView.getRemoteObject();
      remoteViewFactory = remoteView.getRemoteViewFactory();
      if (remoteViewFactory == this) {
        return remoteLuaObject;
      }
    }
    if (remoteLuaObject instanceof LuaInstance) {
      Object javaInstance = ((LuaInstance<?>) remoteLuaObject).getDelegate();
      return converters.toLua(javaInstance);
    }
    if (remoteLuaObject instanceof JavaInstanceWrapper) {
      Object javaInstance = ((JavaInstanceWrapper<?>) remoteLuaObject).getJavaInstance();
      return converters.toLua(javaInstance);
    }
    final ViewFactory finalRemoteViewFactory = remoteViewFactory;
    if (remoteLuaObject instanceof LuaFunction) {
      LuaFunction remoteFunction = (LuaFunction) remoteLuaObject;
      return cache.asMap().computeIfAbsent(remoteFunction,
          k -> new FunctionView(remoteFunction, finalRemoteViewFactory));
    }
    if (remoteLuaObject instanceof Table) {
      Table remoteTable = (Table) remoteLuaObject;
      return cache.asMap().computeIfAbsent(remoteTable,
          k -> new TableView(remoteTable, finalRemoteViewFactory, this));
    }
    return remoteLuaObject;
  }
}
