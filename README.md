Java-OpenGL-Fluid-Simulation
============================

This is a 3D real time fluid simulation with graphics rendered using Java OpenGL (JOGL). I created the first version 
of tbis my junior year of high school for our AP compsci class. This is the second version, the main improvement being
a smoothing function that keeps the surface smoother at the price of reducing the visual effect of ripples. It should be
noted that the algorithm behind it is to treat the simulation as a rectangular height map and use Newton's laws to
distribute water; it simulates waves very well, but there is no simulation of turbulence or anything going on below
the visual surface.

RUNNING:
Simply download the .jar file and double click. It will open to a pre-configured setup, including droplets falling into
the body of water. To reset the simulation press the 'r' key. This will bring up a popup for the size of the simulation.
You probably will get substantial lag with anything beyond 200x200. I also apologize, but the UI is extremely buggy,
so many non-square rectangles will cause the program to freeze and crash. Click okay, this will bring you to another 
popup, this one with many check boxes. The simplest is "enable rain" which sets whether or not the simulation will
have droplets falling in. The following are labeled with two coordinates, which represent a 50x50sub-square of the tank.
If a box is selected, that section will start elevated above the base water level- this is what will give the fluid
waves (when you hit go these oddly shaped dams will all break). Click okay and watch the show, repeat if desirable.

