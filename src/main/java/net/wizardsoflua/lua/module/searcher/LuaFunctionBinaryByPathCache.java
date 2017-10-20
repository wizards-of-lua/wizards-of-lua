package net.wizardsoflua.lua.module.searcher;

import java.io.IOException;
import java.nio.file.Path;

import net.wizardsoflua.lua.compiler.LuaFunctionBinary;

public interface LuaFunctionBinaryByPathCache {

  LuaFunctionBinary get(Path key) throws IOException;

  void put(Path key, LuaFunctionBinary fn) throws IOException;

}
