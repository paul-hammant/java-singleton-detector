
Google Singleton Detector (0.7.3)
===============================

Google Singleton Detector (GSD) is an Open Source tool which allows you to
locate singletons and global state inside Java applications.  Ideally, you will
be able to use this information to locate heavily used global state and
remove it.


Downloading
===========

The latest version of GSD is available at 

  http://code.google.com/p/google-singleton-detector/downloads/list

and may be downloaded in the following distributions:

  gsd-X.X.X.zip - The complete release containing compiled jars and external
  jars.  Simply unzip the file and run GSD.jar.

  gsd-src-X.X.X.zip - A source only distribution which must be built using
  ant.  Includes precompiled external jars.


Building GSD
============

Building GSD currently requires ant 1.7, available at:

  http://ant.apache.org/

and Java 1.5, available at:
  
  http://java.sun.com/javase/downloads/index.jsp

To build the project, enter 'ant' into the directory you unzipped the source
to.  This will create the directory 'target' with a gsd-X.X.X.zip file inside.
Other ant commands:

  ant clean         - Remove generate files
  ant compile       - Build source
  ant compile-tests - Build tests
  ant test          - Build and run tests
  ant jar           - Build the distributable jar
  ant zip           - Build the distributable zip


Usage
=====

Running GSD currently requires Java 1.5, available at:

  http://java.sun.com/javase/downloads/index.jsp

Unzip gsd-X.X.X.zip and run with the following command:

  java -jar sd.jar [-(VvshmfoSb)] [-t <threshold>] <classes dir/jar> <output file> [<package>]
   -V       - Print version and exit
   -v       - Enable verbose mode
   -s       - Hide singletons
   -h       - Hide hingletons
   -m       - Hide mingletons
   -f       - Hide fingletons
   -o       - Hide others
   -S       - Print statistics upon completion
   -b       - Add stats banner to the graph
   -t <val> - Threshold (minimum edges required to draw a node)

The most important options here are probably s, h, m and f, which when included
prevent the program from finding certain types of _ingletons.

The <class dir/jar> parameter is the directory or jar which contains the
classes you wish to analyze. The <output file> should be a .graphml file to
allow your graph viewer to recognize the format. The <package> parameter may
be included to limit the analyzed classes to a certain package.


Viewing the Graph
=================

Once you have a GraphML file, you'll need yEd to view it (we use yWorks to
specify node color and shape, which can be read by yEd). yEd is a graph viewer
and editor which can handle large graphs and provide custom layouts quickly,
and can be found here:

  http://www.yworks.com/en/products_yed_about.htm

Since the GraphML files produced by this program don't define the size of nodes
or the overall layout, you'll have to do a few things to make it readable.

First, you need to resize the nodes (Tools -> Fit Node to Label).

Then apply some type of layout; I find smart organic to be particularly useful
(Layout -> Organic -> Smart), but circular and hierarchical can be very
effective depending on your graph.


Understanding the Graph
=======================

Once you have a graph open and have applied a layout, you'll see a collection
of colorful nodes and arrows.  If you're running this on a small codebase or
one with little use of global state, hopefully you'll see only a few nodes.
If you see a large tangle of lines and colors, don't worry, yEd makes it easy
to work with.

There are six different types of nodes:

+---------+---------+---------------------------------------------------------+
|  Color  |  Class  |                     Description                         |
+---------+---------+---------------------------------------------------------+
|         |         |A class for which there should only be one instance in   |
|         |         |the entire system at any given time. This program detects|
|   Red   |Singleton|singletons which enforce their own singularity, which    |
|         |         |means they keep one static instance of themselves and    |
|         |         |pass it around through a static getter method.           |
+---------+---------+---------------------------------------------------------+
|         |         |Derived from "helper singleton," a class which turns     |
| Orange  |Hingleton|another class into a singleton by enforcing that class's |
|         |         |singularity.                                             |
+---------+---------+---------------------------------------------------------+
|         |         |Derived from "method singleton" a class which has any    |
| Yellow  |Mingleton|static method that returns some state without taking any |
|         |         |parameters.                                              |
+---------+---------+---------------------------------------------------------+
|  Green  |Fingleton|Derived from "field singleton," a class which contains a |
|         |         |public static field.                         |
+---------+---------+---------------------------------------------------------+
| Lt Blue |  Other  |Any class which is directly dependent on a _ingleton.   |
| (Oval)  |         |                                                         |
+---------+---------+---------------------------------------------------------+
|         |         |Displays statistics about the current codebase.  Is only |
|  Blue   |    -    |drawn if the banner option (-b) is passed as a command   |
|         |         |line argument.
+---------+---------+---------------------------------------------------------+

Each node contains two lines of text; the first line is the name of the class
that the node represents, and the second line is the package that class is in
(this saves horizontal space).  If a package was passed as an argument at
runtime, this prefix will be omitted from the package name (again, to save
space).

An arrow is drawn from one node to another if the first class directly uses
the global state of the second.  In the case of singletons and hingletons,
this means that a class calls the getInstance() method of the singleton/
hingleton.  For mingletons and fingletons, this means that a class access the
non-primitive global state of another class.


Additional Information
======================

If you need any more information about GSD, or want to submit a bug or
contribute in any way, check out the Google Code page at:

  http://code.google.com/p/google-singleton-detector/

You can also reach me directly at derubel@gmail.com.
