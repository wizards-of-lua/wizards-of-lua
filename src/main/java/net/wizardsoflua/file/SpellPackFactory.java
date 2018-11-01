package net.wizardsoflua.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.wizardsoflua.WizardsOfLua;

public class SpellPackFactory {
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private final Path tempDirPath;
  private final Path sharedLibraryDirPath;

  public SpellPackFactory(Path tempDirPath, Path sharedLibraryDirPath) {
    this.tempDirPath = tempDirPath;
    this.sharedLibraryDirPath = sharedLibraryDirPath;
  }

  public SpellPack createSpellPack(String fileReference, File directory) throws IOException {
    Path srcDirPath = directory.toPath();
    File targetFile = Files.createTempFile(tempDirPath, directory.getName(), ".jar").toFile();
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile));

    byte[] b = new byte[1024];
    List<Path> pathList = Files.walk(srcDirPath, FileVisitOption.FOLLOW_LINKS)
        .filter(path -> !Files.isDirectory(path)).filter(path -> !isHidden(srcDirPath, path))
        .collect(Collectors.toList());
    for (Path path : pathList) {
      String name = sharedLibraryDirPath.relativize(path).toString();
      out.putNextEntry(new ZipEntry(name));
      FileInputStream in = new FileInputStream(path.toFile());
      int count;
      while ((count = in.read(b)) > 0) {
        out.write(b, 0, count);
      }
      in.close();
    }

    out.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
    writeLine(out, "Manifest-Version: 1.0");
    writeLine(out, "Created-By: " + WizardsOfLua.NAME + " " + WizardsOfLua.VERSION);
    writeLine(out, "Build-Time: " + format(LocalDateTime.now()));
    writeLine(out, "Wol-Auto-Startup: true");

    out.close();
    return new SpellPack(fileReference, targetFile);
  }

  private boolean isHidden(Path relativeParent, Path path) {
    Path relPath = relativeParent.relativize(path);
    int count = relPath.getNameCount();
    for (int i = 0; i < count; ++i) {
      Path name = relPath.getName(i);
      if (name.toString().startsWith(".")) {
        return true;
      }
    }
    return false;
  }

  private void writeLine(ZipOutputStream out, String line)
      throws IOException, UnsupportedEncodingException {
    out.write((line + "\n").getBytes(StandardCharsets.UTF_8.name()));
  }

  private String format(LocalDateTime time) {
    return time.format(formatter);
  }

}
