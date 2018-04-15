package net.wizardsoflua.lua.view;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import net.sandius.rembulan.Table;
import net.sandius.rembulan.runtime.IllegalOperationAttemptException;

public class TableView extends Table implements View {
  private final Table remoteTable;
  private final ViewFactory remoteViewFactory;
  private final ViewFactory localViewFactory;

  public TableView(Table remoteTable, ViewFactory remoteViewFactory,
      ViewFactory localViewFactory) {
    checkArgument(localViewFactory != remoteViewFactory,
        "Trying to create remote view of a local object");
    this.remoteTable = requireNonNull(remoteTable, "remoteTable == null!");
    this.remoteViewFactory = requireNonNull(remoteViewFactory, "remoteViewFactory == null!");
    this.localViewFactory = requireNonNull(localViewFactory, "localViewFactory == null!");
  }

  @Override
  public Object getRemoteObject() {
    return remoteTable;
  }

  @Override
  public ViewFactory getRemoteViewFactory() {
    return remoteViewFactory;
  }

  private Object getRemoteView(Object localLuaObject) {
    return remoteViewFactory.getView(localLuaObject, localViewFactory);
  }

  private Object getLocalView(Object remoteLuaObject) {
    return localViewFactory.getView(remoteLuaObject, remoteViewFactory);
  }

  @Override
  public Table getMetatable() {
    String className = remoteViewFactory.getClassName(remoteTable);
    if (className != null) {
      return localViewFactory.getClassTableForName(className);
    }
    return null;
  }

  @Override
  public Table setMetatable(Table mt) {
    throw new IllegalOperationAttemptException(
        "attempt to set the metatable of a table from a different spell");
  }

  @Override
  public Object rawget(long idx) {
    Object remoteResult = remoteTable.rawget(idx);
    return getLocalView(remoteResult);
  }

  @Override
  public Object rawget(Object localKey) {
    Object remoteKey = getRemoteView(localKey);
    Object remoteResult = remoteTable.rawget(remoteKey);
    return getLocalView(remoteResult);
  }

  @Override
  public void rawset(long idx, Object localValue) {
    Object remoteValue = getRemoteView(localValue);
    remoteTable.rawset(idx, remoteValue);
  }

  @Override
  public void rawset(Object localKey, Object localValue) {
    Object remoteKey = getRemoteView(localKey);
    Object remoteValue = getRemoteView(localValue);
    remoteTable.rawset(remoteKey, remoteValue);
  }

  @Override
  public long rawlen() {
    return remoteTable.rawlen();
  }

  @Override
  public Object initialKey() {
    Object remoteResult = remoteTable.initialKey();
    return getLocalView(remoteResult);
  }

  @Override
  public Object successorKeyOf(Object localKey) {
    Object remoteKey = getRemoteView(localKey);
    Object remoteResult = remoteTable.successorKeyOf(remoteKey);
    return getLocalView(remoteResult);
  }

  @Override
  protected void setMode(boolean weakKeys, boolean weakValues) {
    // no-op
  }
}
