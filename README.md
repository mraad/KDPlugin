KDPlugin
========

This Minecraft plugin enables the visualizaion of a spatial kernel density calculation performed by a Hadoop job whose output is a BigMemory collection set.
The visualization is perform in a world that is dynamically generated from a raster file.

# Dependencies

This project depends on [RasterLib](https://github.com/mraad/RasterLib) and [KDBigMemory](https://github.com/mraad/KDBigMemory).
In addition, this is based on [CraftBukkit for Minecraft 1.6.2 DEVELOPMENT](http://dl.bukkit.org/downloads/craftbukkit/list/dev/) and the [Multiverse plugin](http://dev.bukkit.org/bukkit-plugins/multiverse/)

I am assuming that you have you own Minecraft server. If not, [here](http://wiki.bukkit.org/Setting_up_a_server) are some instructions on how to setup one.
Again, this based on [dev build jar](http://dl.bukkit.org/downloads/craftbukkit/list/dev/).

In my case, I created a folder named 'BukkitServer-1.6.2' in my home folder and start the server from the command line while in that folder as follows:

    $ java -Xmx1024M -jar craftbukkit-1.6.2-R0.1-20130728.195750-15.jar -o true

First time through, the server will download the required resources and will create any supporting folders such as the 'plugins' folder where, as the name indicates, plugins will be installed.

# Build and package

Before building this plugin:

- Stop the craftbukkit server (if running)
- Package and install RasterLib and KDBigMemory
- Copy the included raster float and header files to your home folder


    $ mvn package

Copy the packaged jar to the craftbukkit server plugins folder, and any dependencies to the _libs_ subfolder (This folder will not exist first time through, make sure to create it).
In case case, here is what I do:

    $ cp target/KDPlugin-1.0-SNAPSHOT.jar ~/BukkitServer-1.6.2/plugins/
    $ cp target/libs/*.jar ~/BukkitServer-1.6.2/plugins/libs/

Start the craftbukkit, and it will load at startup the newly installed plugin.

To generate the basemap visualization world, I rely on the Multiverse plugin and the included raster float file.

    > mv create infousa normal -g KDPlugin:/your/home/folder/infousa.flt -t flat

This will generate a normal flat world named _infousa_ that you can teleport to using the Minecraft application.

To load the BigMemory collection set into that world, issue the following:

    > bm infousa 1 40000 15

The command _bm_ is defined in the plugin and expects 4 arguments:

- BigMemory collection set prefix
- color of the blocks
- maximum cell value
- maximum block stack height

The execution of the command connects to the BigMemory server and read the items defined in the _infousa_ set.
A stack of colored blocks will be created at the item geolocation and the height of the stack is proportional to the item value.
