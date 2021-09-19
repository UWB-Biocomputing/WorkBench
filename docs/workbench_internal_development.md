## 4. Internal Development

### 4.1. Workbench Style Guide

[comment]: # (TODO: write introduction)

#### 4.1.1. Java

Java code style for Workbench is based primarily on the [Code Conventions for the Java™ Programming Language](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html) (last revised 20 April 1999) from Sun/Oracle. The most notable exception is that Workbench uses a 100-column line width rather than 80-columns to prevent excessive line wrapping.

Javadoc style for Workbench follows the [Javadoc guidelines](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html) from Oracle.

Below is a summary of the Java style guide. For full details, see the Checkstyle [configuration file](WorkbenchProject/checkstyle.xml) and Checkstyle [documentation](https://checkstyle.sourceforge.io/checks.html).

* **White space**: One space after control statements (`if (isFoo)`, not `if(isFoo)`), between arguments (`myFunc(foo, bar, baz)`, not `myFunc(foo,bar,baz)`), and around operators (`x += 1`, not `x+=1`). No spaces after method names (i.e. `public void myMethod()`).
* **Indentation**: Always use 4-space indents and **never** use tabs! 
* **Blocks**: Open brace "{" should appear at the end of the same line as the declaration statement and be preceded by a single space.
* **Line wrapping**: Always use a 100-column line width for Java code and Javadoc.
* **Naming**: Class and interface names should use `PascalCase`. Methods and variables should use `camelCase`. Constants (i.e. static final members) should use `UPPER_CASE`. Trailing underscores (i.e. `someVariable_`) should never be used.

#### 4.1.2. XML


### 4.2. Tools and Plugins

#### 4.2.1. Apache Maven


#### 4.2.2. Checkstyle


### 4.3. Implementation Details

#### 4.3.1. ProVis Node Spacing

##### 4.3.1.1. Current Behavior

Currently, when you load a provenance file in a ProVis tab the nodes space out but they don't stop automatically. Rather, you need to manually stop them using the "Stop Vertices" switch. There are a couple issues that stem from this.

If you have detached nodes or detached groups of nodes, and you don't press "Stop Vertices" once they've spaced out, they will drift away from each other (explained later) and out of sight, requiring you to chase after them. Even a single node or a connected graph will drift.
 * This is because of the constants used in the implementation of the force directed graph. Specifically, the ones used in calculating the attractive and repelling forces between nodes. You would expect the net force of a node to gradually approach 0 as things space out, however, with the current set of constants, it doesn't approach 0, thus they drift. With the right set of constants, the net force upon a node should gradually go down to 0.
 * As for the unconnected nodes or groups of nodes, this is because there is only a repelling force calculated between them and the nodes they're not connected to. No attractive force to counter it, resulting in drifting. Still, with the right set of constants, it should gradually go down to 0.

This setup also makes it confusing to open a diff between two nodes, which requires dragging one node to another. If you don't select "Stop Vertices" before dragging, the node you're dragging towards runs away (trying to space itself out). If you select "Stop Vertices" you can drag one on top of another, but once you've closed the diff, they're left sitting on top of each other, and to space them back out, you must unselect "Stop Vertices", selecting it again once they've spaced out.

##### 4.3.1.2. Expected Behavior

Upon loading a provenance file, the force directed graph algorithm will run until everything is spaced out. It must determine by itself when the nodes are spaced out and stop.
 * According to [this](https://cs.brown.edu/people/rtamassi/gdhandbook/chapters/force-directed.pdf) paper, (specifically section 12.2 from page 3 to 4, which covers the force directed graph implementation ProVis uses) it mentions that the criteria for an "aesthetically pleasing graph" is that (1) the lengths of the edges ought to be the same, and (2) the layout should display as much symmetry as possible. I discuss this further in the Jupyter Notebook linked towards the end of this page.

From then on, whenever the user interacts with the nodes (by dragging them) the algorithm should be stopped so they don't have to chase other nodes, and when they're done (once the node being dragged is released), the algorithm should run again to space everything back out, stopping automatically.

Of course, all of this means the "Stop Vertices" toggle will be removed.

##### 4.3.1.3. Jupyter Notebook

I made a Jupyter Notebook [here](https://github.com/king-shak/ForceDirectedGraphSim) containing a force directed graph implementation just like the one used by ProVis. It sets up a bunch of nodes in random locations, creating edges to form multiple groups of them and links the groups together. It then runs the force directed graph algorithm to space everything out (the simulation portion). Doing this allowed me to
 * Visualize data (e.g., net forces of the nodes throughout the simulation), so I could get a better understanding of how this algorithm works.
 * Understand why the current set of constants used in calculating the attractive and repelling forces don't work as expected and have a way to find a proper set of constants.

At the end of the notebook is a Discussion covering most of the things mentioned here and my discoveries in finding a method to automatically detect when the nodes have been spaced out - that bit in partiuclar you should look at if you are trying to solve this issue. It also goes over how to find the right set of constants.

**Please update the documentation when this issue is resolved!**