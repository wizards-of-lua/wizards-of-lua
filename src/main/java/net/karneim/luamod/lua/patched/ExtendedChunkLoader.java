package net.karneim.luamod.lua.patched;

import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.load.LoaderException;

public interface ExtendedChunkLoader extends ChunkLoader {
  LuaFunctionBinary compile(String chunkName, String sourceText) throws LoaderException;
}
