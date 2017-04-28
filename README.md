# "Wizards of Lua" Mod

The Wizards of Lua Mod is a modification of Minecraft. It adds the `/lua` command to the game.

Since this project still is in development there is no public download link for the binary available right now.
We are still working hard to get some stable version.
However, if you are interested in it anyway you could of course build it yourself from the sources.

More information about this mod is available at the [Wizards of Lua homepage](http://www.wizards-of-lua.net).

## How to Contribute
This mod is based on Forge, which is a framework for creating and running Minecraft mods based on the Java version of Minecraft.
To contribute to this project you should be familiar with Java 8, Forge, Lua, and Gradle.

Please be aware of the fact that is project is a spare time project. If you post an issue or create a pull request please give us some time to react.

Right now there are no automated tests. We definitely what to add automated tests when we think the Lua Api and the in-game command API is stable. To do that, we expect to rewrite everything from scratch. So maybe you would like to help us with that?

## How to Setup a Local Development Environment for Eclipse
Download the project sources:
* Clone this repository

Then open a terminal, change into the directory and run:
* `gradlew setupDecompWorkspace`
* `gradlew eclipse`

Now import the project into Eclipse:
* Open Eclipse and execute "File > Import > Existing Projects into Workspace"
* Choose the project directory

## How to Run Minecraft from Eclipse
* Add a runtime configuraton to the project
* Set `GradleStart` as main class
* Add the following VM arguments: `-DFORGE_FORCE_FRAME_RECALC=true`
* Set the working directory to: `${workspace_loc:wizards-of-lua}/run`

## How to Create a Mod Binary
* Execute `gradlew build`

## How to Install the Mod into Minecraft 
* Install Forge
* Copy the mod JAR file found in `build/libs` into the `minecraft/mods` folder
* Start Minecraft using the Forge profile

