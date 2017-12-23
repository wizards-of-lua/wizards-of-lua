package net.wizardsoflua.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;
import net.freeutils.httpserver.HTTPServer.VirtualHost;
import net.wizardsoflua.config.RestConfig;
import net.wizardsoflua.file.LuaFile;

public class WolRestServer {

  public interface Context {
    LuaFile getLuaFileByReference(String fileref);

    RestConfig getRestConfig();

    void saveLuaFileByReference(String fileref, String content);
  }

  private Context context;

  public WolRestServer(Context context) {
    this.context = checkNotNull(context, "context==null!");
  }

  public void start() throws IOException {
    int port = context.getRestConfig().getPort();
    File contextRoot = context.getRestConfig().getWebDir();
    HTTPServer server = new HTTPServer(port);
    VirtualHost host = server.getVirtualHost(null);

    // TODO remove debug output
    System.out.println("Serving files from " + contextRoot.getAbsolutePath());

    host.addContext("/", new HTTPServer.FileContextHandler(contextRoot, "/"));
    host.addContexts(new RestHandlers());
    server.start();
  }

  class LuaFileJson {
    public final String path;
    public final String name;
    public final String reference;
    public final String content;

    public LuaFileJson(String path, String name, String reference, String content) {
      this.path = path;
      this.name = name;
      this.reference = reference;
      this.content = content;
    }
  }

  class FileJson {
    public final String path;
    public final String name;
    public final String type;
    public final List<FileJson> files;

    public FileJson(String path, String name, String type, List<FileJson> files) {
      this.path = path;
      this.name = name;
      this.type = type;
      this.files = files;
    }
  }

  class RestHandlers {

    @HTTPServer.Context(value = "/wol/lua", methods = {"GET"})
    public int get(Request req, Response resp) throws IOException {
      try {
        if (acceptsHtml(req)) {
          return sendEditor(req, resp);
        } else if (acceptsJson(req)) {
          return sendLuaFile(req, resp);
        } else {
          String accepts = req.getHeaders().get("Accept");
          resp.sendError(503,
              "Only html and json are supported, but your requests only accepts " + accepts);
          return 0;
        }
      } catch (Exception e) {
        e.printStackTrace();
        resp.sendError(500, e.getMessage());
        return 0;
      }
    }

    @HTTPServer.Context(value = "/wol/lua", methods = {"POST"})
    public int postWizard(Request req, Response resp) throws IOException {
      try {
        JsonParser parser = new JsonParser();
        JsonObject root = parser.parse(IOUtils.toString(req.getBody())).getAsJsonObject();
        Pattern pattern = Pattern.compile("/wol/lua/(.+)");
        Matcher matcher = pattern.matcher(req.getPath());
        if (matcher.matches()) {
          String fileref = matcher.group(1);

          context.saveLuaFileByReference(fileref, root.get("content").getAsString());
          resp.send(200, "OK");
          return 0;
        } else {
          throw new RuntimeException("Unexpected path: '" + req.getPath() + "'");
        }
      } catch (Exception e) {
        e.printStackTrace();
        resp.sendError(500, e.getMessage());
        return 0;
      }
    }

    private int sendLuaFile(Request req, Response resp) throws IOException {
      Pattern pattern = Pattern.compile("/wol/lua/(.+)");
      Matcher matcher = pattern.matcher(req.getPath());
      if (matcher.matches()) {
        String fileref = matcher.group(1);
        LuaFile luaFile = context.getLuaFileByReference(fileref);
        resp.getHeaders().add("Content-Type", "application/json");

        LuaFileJson luaFileJson = new LuaFileJson(luaFile.getPath(), luaFile.getName(),
            luaFile.getFileReference(), luaFile.getContent());
        Gson gson = new Gson();
        String content = gson.toJson(luaFileJson);
        InputStream body =
            new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
        long[] range = null;
        resp.sendHeaders(200, content.length(), -1L, null, "application/json", range);
        resp.sendBody(body, content.length(), null);
        return 0;
      } else {
        throw new RuntimeException("Unexpected path: '" + req.getPath() + "'");
      }
    }

    private int sendEditor(Request req, Response resp) throws IOException {
      Path webDir = Paths.get(new File(context.getRestConfig().getWebDir(), "editor.html").toURI());
      String content = new String(Files.readAllBytes(webDir));

      InputStream body = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8.name()));
      long[] range = null;
      resp.sendHeaders(200, content.length(), -1L, null, "text/html", range);
      resp.sendBody(body, content.length(), null);
      return 0;
    }

    private boolean acceptsJson(Request req) {
      return acceptsMimetype(req, Sets.newHashSet("application/json", "json"));
    }

    private boolean acceptsHtml(Request req) {
      return acceptsMimetype(req, Sets.newHashSet("text/html", "html"));
    }

    private boolean acceptsMimetype(Request req, Set<String> expectedMt) {
      String accept = req.getHeaders().get("Accept");
      for (String actualMt : accept.split(",")) {
        if (expectedMt.contains(actualMt.trim().toLowerCase())) {
          return true;
        }
      }
      return false;
    }
  }

}
