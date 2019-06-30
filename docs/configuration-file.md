---
title: The Wizards of Lua Configuration File
layout: default
---
*The Wizards of Lua Mod can be configured by editing its configuration file.*

The Wizards of Lua configuration file is located at

    <minecraft>/conf/wizards-of-lua/wizards-of-lua.luacfg

where &lt;minecraft&gt; is your Minecraft folder.

Please note that you should only edit it manually when Minecraft is not running.
Otherwise your changes will be ignored and might get overwritten when the mod's configuration has been changed by some [/wol command](/wol-command.html).

## Example
Here is an example of the <tt>wizards-of-lua.luacfg</tt>:
```lua
General {
  luaTicksLimit=50000,
  eventListenerLuaTicksLimit=50000,
  showAboutMessage=true,
  luaLibDirHome="libs",
  sharedLibDir="shared",
  gitHubAccessToken="5fb14281daac1ff4f16fd3e2adafbf91db3b9123" }
RestApi {
  hostname="example.com",
  port=60080,
  secure=true,
  keystore="ssl-keystore.jks",
  keystorePassword="123456",
  keyPassword="123456",
  webDir="www",
  uuid="fd19a362-04fa-4a92-9481-cd21a85c44d8",
  apiKey="sVu4QB1joXfQCM_DUAcFpw" }
ScriptGateway {
  enabled=true,
  timeoutMillis=2000,
  dir="scripts" }
Wizard {
  id="0c77f1a8-943b-4f7b-aa09-f2b1737d4f03",
  libDir="0c77f1a8-943b-4f7b-aa09-f2b1737d4f03",
  apiKey="8lpZ81w7AGWfKCRLHSZtvA" }
Wizard {
  id="d26feccb-baae-4d90-8e3d-50389e8b8ad9",
  libDir="d26feccb-baae-4d90-8e3d-50389e8b8ad9",
  apiKey="9cssfWocUNoWVhA7-m50Cw" }
```

## Format
The contents of <tt>wizards-of-lua.luacfg</tt> is valid Lua code which is loaded and executed by Wol at server startup.
"General", "RestApi", and "Wizard" are internal functions that accept a Lua table as parameter.
All values (with the exception of the wizard id) are optional - in that sense that you can omit the assignment, but on the next startup Wol will choose sensible default values and store them into this file.

<a name="General" style="position:relative; top:-70px; display:block;"></a>
### General
This function sets the mod's general configuration.
* **luaTicksLimit**: defines the number of Lua ticks a spell can run continuously before it must sleep at least for one game tick.
This value can be modified from within the game by [/wol luaTicksLimit](/wol-command.html#Lua-Ticks-Limit).
* **eventListenerLuaTicksLimit**: defines the number of Lua ticks an event listener can run per event.
This value can be modified from within the game by [/wol eventListenerLuaTicksLimit](/wol-command.html#Lua-Ticks-Limit).
* **showAboutMessage**: defines, whether the mod's about message is shown to new users when they log into the server.
Please note, that until the next server restart the about message is only shown once to each user.
* **luaLibDirHome**: defines the relative or absolute location of the home of all Lua library directories.
This directory is parent to the shared lib and all user-specific library directories.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).
* **sharedLibDir**: defines the relative or absolute location of the shared library directory.
This directory is parent of all Lua modules that can be used by all spell.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).
* **gitHubAccessToken**: defines the [GitHub Access Token](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/#creating-a-token)
this Minecraft server will use when a player [downloads a Gists](/wol-command#Import-Gist).
By using an access token you can increase the request rate limit from 50 to 5000 requests per hour.
Default is "".


### RestApi
This function sets the mod's REST server configuration.
The REST server is running inside your Minecraft server and provides HTTP (or HTTPS) access to your Lua files.
All read and write access is protected by a combination of the server's and the player's secret *apiKey*.
However, if you run Wol in a publicly accessible server environment, this is not sufficient in terms of security, since
the key tokens are transmitted in plain text between web browser and REST server.
To protect your keys in a server environment, you should set the *secure* property to "true".
* **hostname**: defines the name of the REST server. This name will be used in the servers HTTP URL.
For example, if you set the hostname to "wizards.example.com", the resulting URL will start with "http://wizards.example.com", or "https://wizards.example.com" respectively.
To make the REST server only accessible from your local computer, set this to "127.0.0.1". Default is "127.0.0.1".
* **port**: defines the port number of the REST server. This number will be used in the servers HTTP URL.
Default is "60000".
* **secure**: defines whether the REST server should use Transport Layer Security (TLS aka. SSL).
If this is set to "true", the REST server is only accessible through HTTPS, and the following properties must also be defined: keystore, keystorePassword, keyPassword.
Setting this to "false" is totally OK in a single player environment.
However, if you plan to run Wol in a server environment, you should really consider setting this to "true" since this will protect your Lua files from unwanted read and write access.
Default is "false".
* **keystore**: this is the filename of the keystore that contains the REST server's SSL certificate.
If provided, the keystore file must be placed next to the "server.properties" file.
This is only used if *secure* is set to "true".
Default is "".
There is a brief description about [how to create a self-signed SSL certificate](/creating-a-self-signed-ssl-certificate.html) and how to use it with WoL.
* **keystorePassword**: this is the password that the REST server should use to access the keystore.
This is only used if *secure* is set to "true".
Default is "".
* **keyPassword**: this is the password that the REST server should use to read the server's SSL certificate from the keystore.
This is only used if *secure* is set to "true".
Default is "".
* **webDir**: this is the filesystem path to the directory where the REST server caches static files.
Its contents are deleted and recreated when the Minecraft server starts.
This directory will be inside the mod's configuration directory.
Default is "www".
* **uuid**: this is the REST servers UUID. This is currently not used, but may be used in a future version of this mod for
authentication purpose.
* **apiKey**: this is the server's randomly generated key that is used for authenticating a REST client (e.g. the web browser). This is used if you want to edit Lua files with your web browser. You should not edit its value. If you want to change it, please do so by deleting it and restarting the server.


### ScriptGateway
This function sets the mod's script gateway configuration.
* **enabled**: defines whether the script gateway is enabled. If enabled, any spell can use the [System.execute() function](/modules/System#execute) to run any program from the scripts directory on the server's operating system.
Default is false.
* **timeoutMillis**: defines how many milliseconds a spell will wait for the normal termination of the executed program.
When the timeout is reached, Minecraft will try to terminate the program.
* **dir**: defines the relative or absolute location of the directory where all executable scripts and programs are stored.
Default is "scripts".

### Wizard
This function adds a player-specific configuration.
New entries are added automatically when a new player uses the [/lua command](/lua-command.html) or the [/wol command](/wol-command.html) the first time.
* **id**: is the UUID of the player this configuration belongs to.
* **libDir**: defines the relative or absolute location of the player-specific Lua library directory.
This directory is parent of all Lua modules that can be used by the player.
If defined as relative, it will be located inside the directory defined by <tt>General.luaLibDirHome</tt>.
For more information about this value please have a look into the [tutorial about importing Lua files](/tutorials/importing_lua_files).
* **apiKey**: is a personal and randomly generated key that is used for authenticating a REST client (e.g. the web browser). This is used if you want to edit Lua files with your web browser. You should not edit its value. If you want to change it, please do so by calling ```/wol browser logout``` - this will generate a new random key.
