package net.wizardsoflua.annotation.processor;

import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import net.wizardsoflua.annotation.processor.doc.GenerateLuaDocProcessor;
import net.wizardsoflua.annotation.processor.luaclass.GenerateLuaClassProcessor;
import net.wizardsoflua.annotation.processor.module.GenerateLuaModuleProcessor;

/**
 * Useful for debugging the annotation processors.
 *
 * @author Adrodoc55
 */
public class RunAnnotationProcessor {
  public static void main(String[] args) throws Exception {
    runAnnoationProcessor();
  }

  public static void runAnnoationProcessor() throws Exception {
    String source = "src/main/java";

    Iterable<JavaFileObject> files = getSourceFiles(source);

    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    CompilationTask task =
        compiler.getTask(new PrintWriter(System.out), null, null, null, null, files);
    task.setProcessors(Arrays.asList(//
        new GenerateLuaClassProcessor(), //
        new GenerateLuaDocProcessor(), //
        new GenerateLuaModuleProcessor() //
    ));

    task.call();
  }

  private static Iterable<JavaFileObject> getSourceFiles(String p_path) throws Exception {
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager files = compiler.getStandardFileManager(null, null, null);

    files.setLocation(StandardLocation.SOURCE_PATH, Arrays.asList(new File(p_path)));
    File outputDir = new File("build/apt");
    outputDir.mkdirs();
    files.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(outputDir));
    files.setLocation(StandardLocation.SOURCE_OUTPUT, Arrays.asList(outputDir));

    Set<Kind> fileKinds = Collections.singleton(Kind.SOURCE);
    return files.list(StandardLocation.SOURCE_PATH, "", fileKinds, true);
  }
}
