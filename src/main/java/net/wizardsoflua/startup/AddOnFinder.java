package net.wizardsoflua.startup;

import static java.lang.String.format;
import static net.wizardsoflua.WizardsOfLua.LOGGER;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import javax.inject.Singleton;
import com.google.common.collect.Iterators;

@Singleton
public class AddOnFinder {
  private static final String WOL_AUTO_STARTUP = "Wol-Auto-Startup";
  private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";

  private final StartupModuleFinder startupModuleFinder = new StartupModuleFinder();
  private List<String> startupModules;

  private List<String> findStartupModules() {
    List<String> result = new ArrayList<>();
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Enumeration<URL> resources = classLoader.getResources(MANIFEST_FILE);
      Iterators.forEnumeration(resources).forEachRemaining(res -> {
        try {
          Manifest mf = new Manifest(res.openStream());
          if (isWolAddOn(mf)) {
            Map<String, String> env = new HashMap<>();
            try (FileSystem zipfs = FileSystems.newFileSystem(res.toURI(), env)) {
              Path rootDir = zipfs.getPath("/");
              LOGGER
                  .info(format("Searching WoL add-on at %s for startup modules", zipfs.toString()));
              List<String> modules = startupModuleFinder.findStartupModulesIn(rootDir);
              result.addAll(modules);
            }
          }
        } catch (IOException | URISyntaxException ex) {
          LOGGER.error(format(
              "Error while searching WoL add-ons for startup modules: can't read manifest in %s properly!",
              res.toExternalForm()), ex);
        }
      });
    } catch (IOException e) {
      LOGGER.error(
          format("Error while searching the classpath for manifest files: %s", e.getMessage()), e);
    }
    return result;
  }

  public List<String> getStartupModules() {
    if (startupModules == null) {
      startupModules = findStartupModules();
    }
    return startupModules;
  }

  private boolean isWolAddOn(Manifest manifest) {
    Attributes attr = manifest.getMainAttributes();
    String value = attr.getValue(WOL_AUTO_STARTUP);
    return value != null && Boolean.parseBoolean(value);
  }
}
