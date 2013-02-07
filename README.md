Java-OpenGL-Fluid-Simulation
============================

This is a 3D real time fluid simulation with graphics rendered using Java OpenGL (JOGL). I created the first version 
of tbis my junior year of high school for our AP compsci class. This is the second version, the main improvement being
a smoothing function that keeps the surface smoother at the price of reducing the visual effect of ripples. It should be
noted that the algorithm behind it is to treat the simulation as a rectangular height map and use Newton's laws to
distribute water; it simulates waves very well, but there is no simulation of turbulence or anything going on below
the visual surface. At the time I was enthralled by visual simulations I found online and the goal was to replicate 
them in some fashion- since it looked like a pretty fluid simulation, the ends justified the means (plus I didn't know
the first thing about Calculus yet).

RUNNING:

Because this program uses the very low level OpenGL system, different builds are required for different machines, and
since each of 10 builds my IDE generated have 5 necessary files, I've loaded them all into a .Rar where they're
sorted into folders by chip family. Alternatively, you can check out this video I made showing a variey of simulations:
http://www.youtube.com/watch?feature=player_detailpage&v=iKTesFgqNt0   (if the link fails it's entitled: Iridescent
3D Fluid Simulation 2).

Download the .Rar file and open the folder with your computer's type of chip, double click on the .jar within.
It will open to a pre-configured setup, including droplets falling into the body of water. To reset the simulation 
press the 'r' key. This will bring up a popup for the size of the simulation. You probably will get substantial
lag with anything beyond 200x200. I also apologize, but the UI is extremely buggy, so many non-square rectangles 
will cause the program to freeze and crash. Click okay, this will bring you to another  popup, this one with many check
boxes. The simplest is "enable rain" which sets whether or not the simulation will have droplets falling in. The
following are labeled with two coordinates, which represent a 50x50sub-square of the tank. If a box is selected, that
section will start elevated above the base water level- this is what will give the fluid waves (when you hit go these
oddly shaped dams will all break). Click okay and enjoy the show, repeat if desired.
