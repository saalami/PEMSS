# PEMSS
Pedestrian Evacuation Modeler, Solver, and Simulator

# Building from source
At the moment,
there is no binary distribution of PEMSS.
You need to have `JDK 1.8+` installed to build the project.
To build from source,
clone the repository:

```
git clone https://github.com/saalami/PEMSS.git
```

Alternatively,
you can download the zip file and extract it.
Then,
`cd` into the project directory and run:

```
./gradlew build
```
if you are on a unix-like system,
or run `gradlew.bat` if you are on windows.
The gradle wrapper,
i.e.,
`gradlew`
takes care of downloading and installing the approperiate version of
gradle if you don't have it installed on your machine.
Once the project is built,
you can execute the following command to run the following command:

```
./gradlew run
```

# Downloading new Maps
PEMSS works with Open Street Map files.
To download a new map,
go to https://www.openstreetmap.org/export
from your browser and zoom in to
the portion of the map that you are interested in.
Avoid downloading larger maps than necessary
since it results in large map files and
longer processing time for the algorithms.
Click on the `Export` button and save the file
as `maps/map.xml` inside the project directory.
You need to restart PEMSS if it is already running
to load the new map.

