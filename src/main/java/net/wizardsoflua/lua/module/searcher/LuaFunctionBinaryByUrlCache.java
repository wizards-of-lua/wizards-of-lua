package net.wizardsoflua.lua.module.searcher;

import java.net.URL;

import net.wizardsoflua.lua.compiler.LuaFunctionBinary;

public interface LuaFunctionBinaryByUrlCache {

  LuaFunctionBinary get(URL key);

  void put(URL key, LuaFunctionBinary fn);

}
