package net.karneim.luamod.lua;

import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import net.karneim.luamod.cache.LuaFunctionBinaryCache;
import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.gist.GistFileRef;
import net.karneim.luamod.gist.GistRepo;
import net.karneim.luamod.lua.patched.ExtendedChunkLoader;
import net.karneim.luamod.lua.patched.LuaFunctionBinary;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class GistSearcher {

  public static void installInto(Table env, ExtendedChunkLoader loader,
      LuaFunctionBinaryCache cache, GistRepo gistRepo, @Nullable Credentials credentials) {
    Table pkg = (Table) env.rawget("package");
    Table searchers = (Table) pkg.rawget("searchers");
    long len = searchers.rawlen();
    searchers.rawset(len + 1, gistLoader(env, loader, cache, gistRepo, credentials));
  }

  private static Object gistLoader(Table env, ExtendedChunkLoader loader,
      LuaFunctionBinaryCache cache, GistRepo gistRepo, @Nullable Credentials credentials) {
    return new GistLoaderFunction(env, loader, cache, gistRepo, credentials);
  }

  private static class GistLoaderFunction extends AbstractFunction1 {

    private final Table env;
    private ExtendedChunkLoader loader;
    private LuaFunctionBinaryCache cache;
    private GistRepo gistRepo;
    @Nullable
    private Credentials credentials;

    public GistLoaderFunction(Table env, ExtendedChunkLoader loader, LuaFunctionBinaryCache cache,
        GistRepo gistRepo, @Nullable Credentials credentials) {
      this.env = env;
      this.loader = loader;
      this.cache = cache;
      this.gistRepo = gistRepo;
      this.credentials = credentials;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String gistRefStr = String.valueOf(arg1);
      GistFileRef gistRef = GistFileRef.parseGistRef(gistRefStr);
      if (gistRef == null) {
        throw new IllegalArgumentException(
            String.format("Expected Gist reference, but got: %s", gistRefStr));
      }
      try {
        LuaFunctionBinary fnBin = cache.get(gistRefStr);
        if (fnBin == null) {
          String content = gistRepo.load(credentials, gistRef);
          fnBin = loader.compile(gistRef.asFilename(), content);
          cache.put(gistRefStr, fnBin);
        }
        LuaFunction fn = fnBin.loadInto(new Variable(env));
        context.getReturnBuffer().setTo(fn, gistRef);
      } catch (Exception e) {
        throw new UndeclaredThrowableException(e);
      }
    }

    @Override
    public void resume(ExecutionContext context, Object suspendedState)
        throws ResolvedControlThrowable {
      throw new NonsuspendableFunctionException();
    }
  }

}
