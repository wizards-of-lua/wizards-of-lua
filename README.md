# "Wizards of Lua" Mod

The Wizards of Lua Mod is a modification of Minecraft. It adds the `/lua` command to the game.

More information about this mod is available at the [Wizards of Lua homepage](http://www.wizards-of-lua.net).

## How to Contribute Ideas
If you have any nice idea that you think this mod could benefit from please feel free to tell us about it. You can post ideas, feature requests, questions, or bug reports using the [issue tracker](https://github.com/wizards-of-lua/wizards-of-lua/issues).

Before you post anything, please make sure that you search not only open issues but also [close issues](https://github.com/wizards-of-lua/wizards-of-lua/pulls?q=is%3Aclosed%20) for simmilar posts to prevent duplicate posts. 

## How to Contribute Source Code
We really welcome code contributions, but please be aware that we are a bit 'picky'. We won't include things that we think are not matching our vision of this project.

This mod is based on Forge, which is a framework for creating and running Minecraft mods based on the Java version of Minecraft.
To contribute source code to this project you should be familiar with Java 8, Forge, Lua, and Gradle.

Please not that is project is a spare time project. If you post an issue or create a pull request please give us some time to react.

## How to Setup a Local Development Environment for Eclipse
Download the project sources using Git from the command line:
* `git clone https://github.com/wizards-of-lua/wizards-of-lua.git`

Then change into the `wizards-of-lua` directory and run:
* `gradlew setupDecompWorkspace`
* `gradlew eclipse`

Now import the project into Eclipse:
* Open Eclipse and execute "File > Import > Existing Projects into Workspace"
* Choose the `wizards-of-lua` directory

## How to Run Minecraft from Eclipse
* Add a runtime configuration to the project
* Set `GradleStart` as main class
* Add the following VM arguments: `-DFORGE_FORCE_FRAME_RECALC=true`
* Set the working directory to: `${workspace_loc:wizards-of-lua}/run`

## How to Create a Mod Binary
* Execute `gradlew clean assemble`

Please note that you can not use the `build` task right now since the automated test will not
run successfully when executed from Gradle.

As a result you will find the mod JAR file in `build/libs`.

## How to Install the Mod into Minecraft 
* Install Forge
* Copy the mod JAR file found in `build/libs` into the `minecraft/mods` folder
* Start Minecraft using the Forge profile

## License
Wizards of Lua is licensed under the GNU Lesser General Public License v3.0. See the file [LICENSE](LICENSE) file for details.

Wizards of Lua uses the great [Rembulan](https://github.com/mjanicek/rembulan) implementation of [Lua](https://www.lua.org) 5.3 for Java, which is licensed under the [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0). 
