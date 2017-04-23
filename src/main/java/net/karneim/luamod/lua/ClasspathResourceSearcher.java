package net.karneim.luamod.lua;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import net.karneim.luamod.cache.LuaFunctionBinaryCache;
import net.karneim.luamod.lua.patched.ExtendedChunkLoader;
import net.karneim.luamod.lua.patched.LuaFunctionBinary;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.impl.NonsuspendableFunctionException;
import net.sandius.rembulan.runtime.AbstractFunction1;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;

public class ClasspathResourceSearcher {

  public static void installInto(Table env, ExtendedChunkLoader loader,
      LuaFunctionBinaryCache cache, ClassLoader classloader) {
    Table pkg = (Table) env.rawget("package");
    Table searchers = (Table) pkg.rawget("searchers");
    long len = searchers.rawlen();
    searchers.rawset(len + 1, getFunction(env, loader, cache, classloader));
  }

  private static Object getFunction(Table env, ExtendedChunkLoader loader,
      LuaFunctionBinaryCache cache, ClassLoader classloader) {
    return new LoaderFunction(env, loader, cache, classloader);
  }

  private static class LoaderFunction extends AbstractFunction1 {

    private final Table env;
    private ExtendedChunkLoader loader;
    private LuaFunctionBinaryCache cache;
    private ClassLoader classloader;

    public LoaderFunction(Table env, ExtendedChunkLoader loader, LuaFunctionBinaryCache cache, ClassLoader classloader) {
      this.env = env;
      this.loader = loader;
      this.cache = cache;
      this.classloader = classloader;
    }

    @Override
    public void invoke(ExecutionContext context, Object arg1) throws ResolvedControlThrowable {
      String moduleName = String.valueOf(arg1);
      if (moduleName == null) {
        throw new IllegalArgumentException(
            String.format("Expected module name, but got: %s", moduleName));
      }
      try {
        LuaFunctionBinary fnBin = cache.get(moduleName);
        if (fnBin == null) {
          String name = moduleName.replaceAll("\\.", "/")+".lua";
          URL resource = classloader.getResource(name);
          if (resource == null) {
            context.getReturnBuffer().setTo("no module with name '"+name+"' found in classpath");
            return;
          }
          String src = IOUtils.toString(resource);
          fnBin = loader.compile(moduleName, src);
          cache.put(moduleName, fnBin);
        }
        LuaFunction fn = fnBin.loadInto(new Variable(env));
        context.getReturnBuffer().setTo(fn, moduleName);
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
