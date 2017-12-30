package net.wizardsoflua.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.freeutils.httpserver.HTTPServer;
import net.freeutils.httpserver.HTTPServer.Request;
import net.freeutils.httpserver.HTTPServer.Response;
import net.freeutils.httpserver.HTTPServer.VirtualHost;
import net.wizardsoflua.WizardsOfLua;
import net.wizardsoflua.config.RestConfig;
import net.wizardsoflua.file.LuaFile;

public class WolRestServer {

  public interface Context {
    LuaFile getLuaFileByReference(String fileref);

    RestConfig getRestConfig();

    void saveLuaFileByReference(String fileref, String content);
  }

  private final Context context;
  private final Logger logger;


  public WolRestServer(Context context) {
    this.context = checkNotNull(context, "context==null!");
    this.logger = WizardsOfLua.instance.logger;
  }

  public void start() throws IOException {
    logger.debug("[REST] starting WoL REST service");
    File contextRoot = context.getRestConfig().getWebDir();
    int port = context.getRestConfig().getPort();
    boolean secure = context.getRestConfig().isSecure();
    String hostname = context.getRestConfig().getHostname();
    String protocol = context.getRestConfig().getProtocol();
    final String keystore = context.getRestConfig().getKeyStore();
    if (secure) {
      checkNotNull(keystore,
          "Missing keystore! Please configure path to keystore file in WoL config!");
    }
    final char[] keystorePassword = context.getRestConfig().getKeyStorePassword();
    final char[] keyPassword = context.getRestConfig().getKeyPassword();

    createEmptyContextRoot(contextRoot);

    logger.info("[REST] REST service will cache static files at " + contextRoot.getAbsolutePath());

    HTTPServer server = new HTTPServer(port) {
      protected ServerSocket createServerSocket() throws IOException {
        ServerSocketFactory factory =
            this.secure ? createSSLServerSocketFactory(keystore, keystorePassword, keyPassword)
                : ServerSocketFactory.getDefault();
        ServerSocket serv = factory.createServerSocket();
        serv.setReuseAddress(true);
        serv.bind(new InetSocketAddress(port));
        return serv;
      }
    };
    server.setSecure(secure);
    VirtualHost host = new VirtualHost(hostname);
    server.addVirtualHost(host);

    StaticResourceHandlers staticResourceHandlers =
        new StaticResourceHandlers("/www", contextRoot, "/");
    host.addContext("/", staticResourceHandlers);
    host.addContexts(new RestHandlers(staticResourceHandlers));

    logger.info("Starting REST service at " + protocol + "://" + hostname + ":" + port);
    server.start();
  }

  private SSLServerSocketFactory createSSLServerSocketFactory(String keystore,
      char[] keystorePassword, char[] keyPassword) {
    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      KeyStore ks = KeyStore.getInstance("JKS");
      try (InputStream in = new FileInputStream(new File(keystore))) {
        ks.load(in, keystorePassword);
      }
      KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
      kmf.init(ks, keyPassword);
      sslContext.init(kmf.getKeyManagers(), null, null);

      return sslContext.getServerSocketFactory();
    } catch (UnrecoverableKeyException | KeyManagementException | NoSuchAlgorithmException
        | KeyStoreException | CertificateException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void createEmptyContextRoot(File contextRoot) throws IOException {
    Path dir = contextRoot.toPath();
    if (Files.isDirectory(dir)) {
      deleteDir(dir);
    }
    Files.createDirectories(dir);
  }

  private void deleteDir(Path dirPath) throws IOException {
    Files.walk(dirPath) //
        .map(Path::toFile) //
        .sorted(Comparator.comparing(File::isDirectory)) //
        .forEach(File::delete);
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

  class StaticResourceHandlers implements HTTPServer.ContextHandler {

    private final String resourcePath;
    private final File contextRoot;
    // private final HTTPServer.FileContextHandler cacheDirHandler;

    public StaticResourceHandlers(String resourcePath, File contextRoot, String context)
        throws IOException {
      this.resourcePath = resourcePath;
      this.contextRoot = contextRoot;
      // this.cacheDirHandler = new HTTPServer.FileContextHandler(contextRoot);
    }

    @Override
    public int serve(Request req, Response resp) throws IOException {
      String path = req.getPath();
      if (!path.startsWith("/")) {
        return 404;
      }
      Path cachedFilePath = Paths.get(contextRoot.getAbsolutePath(), path);
      if (!Files.exists(cachedFilePath)) {
        String resource = resourcePath + path;
        URL url = WolRestServer.class.getResource(resource);
        if (url == null) {
          WizardsOfLua.instance.logger.warn("WolRestServer couldn't find resource at " + resource);
          return 404;
        }
        File targetFile = new File(contextRoot, path);
        if (!targetFile.getParentFile().exists()) {
          Files.createDirectories(targetFile.getParentFile().toPath());
        }
        IOUtils.copy(url.openStream(), new FileOutputStream(targetFile));
      }
      // return cacheDirHandler.serve(req, resp);
      return HTTPServer.serveFile(contextRoot, "", req, resp);
    }
  }

  class RestHandlers {

    private final StaticResourceHandlers staticResourceHandlers;

    public RestHandlers(StaticResourceHandlers staticResourceHandlers) {
      this.staticResourceHandlers = staticResourceHandlers;
    }

    @HTTPServer.Context(value = "/wol/lua", methods = {"GET"})
    public int get(Request req, Response resp) throws IOException {
      try {
        logger.debug("[REST] " + req.getMethod() + " " + req.getPath());
        if (acceptsJson(req)) {
          return sendLuaFile(req, resp);
        } else if (acceptsHtml(req)) {
          return sendEditor(req, resp);
        } else {
          String accepts = req.getHeaders().get("Accept");
          resp.sendError(503,
              "Only html and json are supported, but your requests only accepts " + accepts);
          return 0;
        }
      } catch (Exception e) {
        logger.error("Error handling GET: " + req.getPath(), e);
        resp.sendError(500, "Couldn't process GET method! e=" + e.getMessage()
            + "\n See fml-server-latest.log for more info!");
        return 0;
      }
    }

    @HTTPServer.Context(value = "/wol/lua", methods = {"POST"})
    public int post(Request req, Response resp) throws IOException {
      try {
        logger.debug("[REST] " + req.getMethod() + " " + req.getPath());
        JsonParser parser = new JsonParser();
        String body = IOUtils.toString(req.getBody(), StandardCharsets.UTF_8.name());
        JsonObject root = parser.parse(body).getAsJsonObject();
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
        logger.error("Error handling POST: " + req.getPath(), e);
        resp.sendError(500, "Couldn't process POST method! e=" + e.getMessage()
            + "\n See fml-server-latest.log for more info!");
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

      req.setPath("/editor.html");

      return staticResourceHandlers.serve(req, resp);
    }

    private boolean acceptsJson(Request req) throws IOException {
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
