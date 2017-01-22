package net.karneim.luamod.lua;

import java.lang.reflect.UndeclaredThrowableException;

import javax.annotation.Nullable;

import net.karneim.luamod.credentials.Credentials;
import net.karneim.luamod.gist.GistFileRef;
import net.karneim.luamod.gist.GistRepo;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.load.ChunkLoader;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class GistSearcher {

  public static void installInto(Table env, ChunkLoader loader, GistRepo gistRepo,
      @Nullable Credentials credentials) {
    Table pkg = (Table) env.rawget("package");
    Table searchers = (Table) pkg.rawget("searchers");
    long len = searchers.rawlen();
    searchers.rawset(len + 1, gistLoader(env, loader, gistRepo, credentials));
  }

  private static Object gistLoader(Table env, ChunkLoader loader, GistRepo gistRepo,
      @Nullable Credentials credentials) {
    return new GistLoaderFunction(env, loader, gistRepo, credentials);
  }

  private static class GistLoaderFunction extends AbstractFunction1 {

    private final Table env;
    private ChunkLoader loader;
    private GistRepo gistRepo;
    @Nullable
    private Credentials credentials;

    public GistLoaderFunction(Table env, ChunkLoader loader, GistRepo gistRepo,
        @Nullable Credentials credentials) {
      this.env = env;
      this.loader = loader;
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
        String content = gistRepo.load(credentials, gistRef);
        LuaFunction fn = loader.loadTextChunk(new Variable(env), gistRef.asFilename(), content);
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
