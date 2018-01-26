---
title: Creating a Self-Signed SSL Certificate
---
*You can use a self-signed SSL certificate to make the Lua file editor secure.*

It you want the internal web server to use HTTPS instead of HTTP, you can easily
create your own self-signed SSL certificate and add it to the configuration.
Of course, when used for securing HTTPS connections, this requires to be accepted
manually by the browser's user.
But fortunately this is only necessary once.

Here is a brief description how you can create your own self-signed SSL certificate,
and how you add it to the WoL configuration.

### Generating a Self-Signed SSL Certificate using Java's Keytool
This requires, that Java is installed on your machine, and that it is accessible
from the command line.

* Open the command line on your computer.
* Execute the following command (but replace the values for CN and OU with whatever you like):
```bash
keytool -genkey -keystore server-keystore.jks -alias server_alias \
        -dname "CN=127.0.0.1,OU=whateveryoulike" \
        -keyalg RSA -sigalg SHA256withRSA -keysize 2048 -validity 365 \
        -storetype pkcs12
```
* When prompted for passwords, just choose some and store them properly in your mind.
You will need to write them into the server's config later on.
* This will generate a new file called ```server-keystore.jks``` in the current directory.

### Adding the SSL-Certificate to your Minecraft Server
* Copy the file ```server-keystore.jks``` into your Minecraft server's directory,
next to the ```server.properties``` file.
* Now edit the WoL configuration file at ```config/wizards-of-lua/wizards-of-lua.luacfg```.
* Scroll down to the *RestApi* section and change the values for *hostname, secure, keystore,
keystorePassword*, and *keyPassword*, for example like this:

```lua
RestApi {
  hostname="example.com",
  port=60080,
  secure=true,
  keystore="ssl-keystore.jks",
  keystorePassword="123456",
  keyPassword="123456",
  ...
```
Please replace *example.com* with the hostname or the numerical IP address of your server.
And replace the values for *keystorePassword* and *keyPassword* with those you chose when
you used the keytool.

* Now restart the Minecraft server
* Now start a Minecraft client and log into your Minecraft server
* Type in ```/wol browser login``` to authenticate your browser for HTTPS.
* And then use ```/wol file edit <filename>``` or ```/wol shared-file edit <filename>``` to edit your files.
