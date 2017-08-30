---
title: Installation
---
Wizards of Lua is a <u>server-side modification</u> of Minecraft.
That means, only the server must be modded.
For clients no modification is required.
However, if you want to play Wizards of Lua in singleplayer, you need to modify
the client as well.

### Requirements
* The Wizards of Lua modification requires Minecraft 1.11.2 (Java)
* and Forge 13.20.1.*

### Download
* [Wizards of Lua](https://minecraft.curseforge.com/projects/wizards-of-lua/files)

### Installing Wizards of Lua on a Server
* [Download Forge Installer](http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.11.2.html) 13.20.1.2454 (or later)
* Copy <tt>forge-1.11.2-13.20.1.2454-installer.jar</tt> into a new and empty directory
where you want to run the server from
* Change into that directory and run
```bash
java -jar forge-1.11.2-13.20.1.2454-installer.jar --installServer
```
This will download additional files from the internet and create the server jar file
* Run vanilla Minecraft with
```bash
java -Xms1024M -jar minecraft_server.1.11.2.jar nogui
```
This will try to start the server.
But since you need to sign the eula, the program aborts shorty afterwards with something like this:
```
[17:04:47] [Server thread/INFO]: You need to agree to the EULA in order to run the server. Go to eula.txt for more info.
[17:04:47] [Server thread/INFO]: Stopping server
[17:04:47] [Server thread/INFO]: Saving worlds
[17:04:47] [Server thread/WARN] [FML]: Can't revert to frozen GameData state without freezing first.
[17:04:47] [Server thread/INFO] [FML]: The state engine was in incorrect state POSTINITIALIZATION and forced into state SERVER_STOPPED. Errors may have been discarded.
[17:04:47] [Server Shutdown Thread/INFO]: Stopping server
[17:04:47] [Server Shutdown Thread/INFO]: Saving worlds
```
* Now open the file <tt>eula.txt</tt> and change the following line:
```
eula=false
```
to
```
eula=true
```
and save the file.
* Now download the [latest version of Wizards of Lua](https://minecraft.curseforge.com/projects/wizards-of-lua/files) and copy it into the <tt>mods</tt>
directory of your server directory.
* Now run Minecraft Forge with
```bash
java -Xms1024M -jar forge-1.11.2-13.20.1.2454-universal.jar nogui
```
This will start the modded version of Minecraft with Forge and Wizards Of Lua activated.
Now you can execute Lua commands.
Let's give it a try:
* Type the following line into the command-line:
```bash
/lua print(1+1)
```
If everything works find you should receive something like the following output:
```
[17:06:13] [Server thread/INFO]: 2
```
* Now start you Minecraft client and log into your new server
* Don't forget to give yourself operator priviliges

### Installing Wizards of Lua on a Client
Please note that installing Wizards of Lua on the client is **not** required if
you only plan to play it on the server.
However, if you want to play Wizards of Lua in singleplayer, you need to modify
the client.
This procedure assumes that you have a copy of Minecraft installed.
Also you must have a working Java Runtime Environment (Version 1.8).
If not, [get Minecraft](https://minecraft.net/),
[get Java](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html),
install both and come back here.

* [Download Forge Installer](http://files.minecraftforge.net/maven/net/minecraftforge/forge/index_1.11.2.html) 13.20.1.2454 (or later)
* Start the Forge Installer by double-clicking on the file
* Choose "Install client" option and click "OK"
* Now download the [latest version of Wizards of Lua](https://minecraft.curseforge.com/projects/wizards-of-lua/files)
* Copy the mod into the <tt>mods</tt> folder of your Minecraft installation
* Run the Minecraft Launcher
* On right side of the "Play" button click on the small green triangle pointing upwards
* Choose the "forge" profile
* Click "Play"
