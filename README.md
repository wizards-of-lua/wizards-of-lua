# "Wizards of Lua" Mod

The Wizards of Lua Mod is a modification of Minecraft. It adds the `/lua` command to the game.

More information about this mod is available at the [Wizards of Lua homepage](http://www.wizards-of-lua.net).

## How to Contribute Ideas
If you have any nice idea that you think this mod could benefit from please feel free to tell us about it.
You can post ideas, feature requests, questions, or bug reports using the [issue tracker](https://github.com/wizards-of-lua/wizards-of-lua/issues).

Before you post anything, please make sure that you search not only open issues but also [closed issues](https://github.com/wizards-of-lua/wizards-of-lua/issues?q=is%3Aclosed) for simmilar posts to prevent duplicate posts.

## How to Contribute Source Code
We really welcome code contributions, but please be aware that we are a bit 'picky'.
We won't include things that we think are not matching our vision of this project.

This mod is based on Forge, which is a framework for creating and running Minecraft mods based on the Java version of Minecraft.
To contribute source code to this project you should be familiar with Java 8, Forge, Lua, and Gradle.

Please note that this project is a spare time project.
If you post an issue or create a pull request, please give us some time to react.

## How to Setup a Local Development Environment for Eclipse
Download the project sources using Git from the command line:
* `git clone https://github.com/wizards-of-lua/wizards-of-lua.git`

Then change into the `wizards-of-lua` directory and run:
* `gradlew eclipse` (Run this twice if you want to be able to see the Minecraft source code in Eclipse)

Now import the project into [Eclipse](https://www.eclipse.org/):
* Open Eclipse and execute "File > Import > Existing Projects into Workspace"
* Choose the `wizards-of-lua` directory
* Enable `Search for nested projects`

## How to Run the Minecraft Client from Eclipse
* Generate the Eclipse launch configuration files with `gradlew genEclipseRuns`
* Refresh the `wizards-of-lua` project in Eclipse
* Run the newly generated file named `runClient.launch`
* Hit `Finish` to import `wizards-of-lua` and it's subprojects

## How to Run the Minecraft Server from Eclipse
* Generate the Eclipse launch configuration files with `gradlew genEclipseRuns`
* Refresh the `wizards-of-lua` project in Eclipse
* Run the newly generated file named `runServer.launch`
* Accept Mojangs end-user license agreement in `wizards-of-lua/run/eula.txt`
* Run `runServer.launch` again
* Once the server is running stop it
* Change the following values in `wizards-of-lua/run/server.properties`:
  * `level-type=DEFAULT` -> `level-type=FLAT`
  * `enable-command-block=false` -> `enable-command-block=true`
  * `online-mode=true`-> `online-mode=false`
* Delete the directory `wizards-of-lua/run/world`
* Run `runServer.launch` again

## How to Create a Mod Binary
* Execute `gradlew clean assemble`

Please note that you can not use the `build` task right now since the automated test will not
run successfully when executed from Gradle.

As a result you will find the mod JAR file in `build/libs`.

## How to Run the Integration Tests
* Join the server and execute the ```/test``` from the player's chat input line.

## How to Install the Mod into Minecraft
* Install Forge
* Copy the mod JAR file found in `build/libs` into the `minecraft/mods` folder
* Start Minecraft using the Forge profile

## License
Wizards of Lua is licensed under the GNU General Public License v3.0. See the file [LICENSE](LICENSE) file for details.

Wizards of Lua uses the great [Rembulan](https://github.com/mjanicek/rembulan) implementation of [Lua](https://www.lua.org) 5.3 for Java, which is licensed under the [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

Wizards of Lua uses JLHTTP, the minimalist's first choice HTTP server created by Amichai Rothman.
JLHTTP is licensed under the GPL 2.0 license. For additional info see http://www.freeutils.net/source/jlhttp/.
