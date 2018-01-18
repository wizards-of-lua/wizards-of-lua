package net.wizardsoflua.lua.module.searcher;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sandius.rembulan.ByteString;
import net.sandius.rembulan.ByteStringBuilder;
import net.sandius.rembulan.Conversions;
import net.sandius.rembulan.LuaRuntimeException;
import net.sandius.rembulan.Table;
import net.sandius.rembulan.Variable;
import net.sandius.rembulan.lib.AbstractLibFunction;
import net.sandius.rembulan.lib.ArgumentIterator;
import net.sandius.rembulan.load.LoaderException;
import net.sandius.rembulan.runtime.ExecutionContext;
import net.sandius.rembulan.runtime.LuaFunction;
import net.sandius.rembulan.runtime.ResolvedControlThrowable;
import net.sandius.rembulan.util.ByteIterator;
import net.wizardsoflua.lua.compiler.ExtendedChunkLoader;
import net.wizardsoflua.lua.compiler.LuaFunctionBinary;

public class PatchedChunkLoadPathSearcher extends AbstractLibFunction {

  public interface Context {
    String getLuaPath();
  }
  
  public static void installInto(Table env, ExtendedChunkLoader loader,
      LuaFunctionBinaryByPathCache cache, ClassLoader classloader, FileSystem fileSystem, Context context) {
    Object function = new PatchedChunkLoadPathSearcher(fileSystem, loader, cache, env, context);
    Table pkg = (Table) env.rawget("package");
    Table searchers = (Table) pkg.rawget("searchers");
    long len = searchers.rawlen();
    searchers.rawset(len + 1, function);
  }

  static final ByteString DEFAULT_MODE = ByteString.constOf("bt");

  private final FileSystem fileSystem;
  private final ExtendedChunkLoader loader;
  private final LuaFunctionBinaryByPathCache cache;
  private final Object env;
  private final Context context;

  public PatchedChunkLoadPathSearcher(FileSystem fileSystem,
      ExtendedChunkLoader loader, LuaFunctionBinaryByPathCache cache, Object env, Context context) {
    this.fileSystem = Objects.requireNonNull(fileSystem);
    this.loader = Objects.requireNonNull(loader);
    this.cache = Objects.requireNonNull(cache);;
    this.env = env;
    this.context = context;
  }

  @Override
  protected String name() {
    return "(path searcher)";
  }

  private LuaFunction loaderForPath(ByteString path) throws LoaderException {
    return loadTextChunkFromFile(path.toString(), PatchedChunkLoadPathSearcher.DEFAULT_MODE);
  }

  @Override
  protected void invoke(ExecutionContext context, ArgumentIterator args)
      throws ResolvedControlThrowable {
    ByteString modName = args.nextString();

    ByteString path = Conversions.stringValueOf(this.context.getLuaPath());
    if (path == null) {
      throw new IllegalStateException("Missing 'path'");
    }

    List<ByteString> paths = SearchPath.getPaths(modName, path, SearchPath.DEFAULT_SEP,
        ByteString.of(fileSystem.getSeparator()));

    ByteStringBuilder msgBuilder = new ByteStringBuilder();
    for (ByteString s : paths) {
      Path p = fileSystem.getPath(s.toString());
      if (Files.isReadable(p)) {
        final LuaFunction fn;
        try {
          fn = loaderForPath(s);
        } catch (LoaderException ex) {
          throw new LuaRuntimeException("error loading module '" + modName + "' from file '" + s
              + "'" + "\n\t" + ex.getLuaStyleErrorMessage());
        }

        context.getReturnBuffer().setTo(fn, s);
        return;
      } else {
        msgBuilder.append("\n\tno file '").append(s).append((byte) '\'');
      }
    }

    context.getReturnBuffer().setTo(msgBuilder.toByteString());
  }

  private LuaFunction loadTextChunkFromFile(String fileName, ByteString modeString)
      throws LoaderException {
    final LuaFunction fn;
    try {
      if (!modeString.contains((byte) 't')) {
        throw new LuaRuntimeException(
            "attempt to load a text chunk (mode is '" + modeString + "')");
      }
      Path p = fileSystem.getPath(fileName);
      LuaFunctionBinary fnBin = cache.get(p);
      if (fnBin == null) {
        // FIXME: this is extremely wasteful!
        byte[] bytes = Files.readAllBytes(p);
        ByteString chunkText = ByteString.copyOf(bytes);

        fnBin = loader.compile(fileName, chunkText.toString());
        cache.put(p, fnBin);
      }
      fn = fnBin.loadInto(new Variable(env));

    } catch (InvalidPathException | IOException ex) {
      throw new LoaderException(ex, fileName);
    }

    if (fn == null) {
      throw new LuaRuntimeException("loader returned nil");
    }

    return fn;
  }

  static class SearchPath extends AbstractLibFunction {

    private static final ByteString DEFAULT_SEP = ByteString.constOf(".");

    private final FileSystem fileSystem;
    private final ByteString defaultDirSeparator;

    SearchPath(FileSystem fileSystem) {
      this.fileSystem = Objects.requireNonNull(fileSystem);
      defaultDirSeparator = ByteString.of(fileSystem.getSeparator());
    }

    @Override
    protected String name() {
      return "searchpath";
    }

    static final byte PATH_SEPARATOR = (byte) ';';
    static final byte PATH_TEMPLATE_PLACEHOLDER = (byte) '?';

    static List<ByteString> getPaths(ByteString name, ByteString path, ByteString sep,
        ByteString rep) {
      List<ByteString> result = new ArrayList<>();

      name = name.replace(sep, rep);

      ByteStringBuilder builder = new ByteStringBuilder();
      ByteIterator it = path.byteIterator();
      while (it.hasNext()) {
        byte b = it.nextByte();
        switch (b) {
          case PATH_TEMPLATE_PLACEHOLDER:
            builder.append(name);
            break;
          case PATH_SEPARATOR:
            result.add(builder.toByteString());
            builder.setLength(0);
            break;
          default:
            builder.append(b);
            break;
        }
      }
      if (builder.length() > 0) {
        result.add(builder.toByteString());
      }
      return result;
    }

    @Override
    protected void invoke(ExecutionContext context, ArgumentIterator args)
        throws ResolvedControlThrowable {
      ByteString name = args.nextString();
      ByteString path = args.nextString();
      ByteString sep = args.nextOptionalString(DEFAULT_SEP);
      ByteString rep = args.nextOptionalString(defaultDirSeparator);

      ByteStringBuilder msgBuilder = new ByteStringBuilder();

      for (ByteString s : getPaths(name, path, sep, rep)) {
        Path p = fileSystem.getPath(s.toString());
        if (Files.isReadable(p)) {
          context.getReturnBuffer().setTo(s);
          return;
        } else {
          msgBuilder.append("\n\tno file '").append(s).append((byte) '\'');
        }
      }
      // no readable file found
      context.getReturnBuffer().setTo(null, msgBuilder.toString());
    }
  }

}
