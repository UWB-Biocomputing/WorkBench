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

#### 4.3.2. Programmer Workflow for Adding/Modifying Template Classes for Graphitti

##### 4.3.2.1. Description of all the Template Files

All the template files are stored in `WorkbenchProject/src/main/resources/templates/`. Here is what each file does/what each directory contains:

 * `templates/`
	 * `BaseTemplates/`
		 * `BaseTemplateDefault.xml` - this contains the skeleton of the config file. Notice the the nodes within `<ModelParams>` are all empty - this is all filled in later from information in the class parameter templates.
	 * `BaseTemplateConfig.xml` - this contains the names of the different *categories* of classes - i.e., Vertices, Edges, Connections, etc. It also specifies the location of these categories of classes in the `nodePath` property.
	 * `ParamsClassTemplateConfig/` - within this directory is a directory for each class category (Vertices, Edges, Connections, etc.), which contains the template files for the specific files. When you start a new simulation in Workbench, the options for the classes on the Simulation Configuration dialog are what you see here. The contents of the corresponding template files (found in these directories) is then used to build the base template from `BaseTemplateDefault.xml`. The parameters you define in the Simulator Configuration dialog are from these template files.
		 * `ConnectionsParamsClass/` - This contains a file per each Connections class laying out its parameters.
		 * `LayoutParamsClass/` - This contains a file per each Layout class laying out its parameters.
		 * `VerticesParamsClass/` - This contains a file per each Vertices class laying out its parameters.
		 * `EdgesParamsClass/` - This contains a file per each Edges class laying out its parameters.
		 * `AllParamsClasses.xml` - This lists the different classes for each class category listed within `BaseTemplateConfig.xml`, specifying the name of the corresponding template file.
		 * `AllParamsClasses.xsd` - This file ensures that the information entered in `AllParamsClasses.xml` is properly structured.

##### 4.3.2.2. How to Add/Remove a New Class Template

To add a new class template, you must

 1. Create the new template file within the respective directory within `ParamsClassTemplateConfig/`,
 2. and create an entry for it in the respective class category in `AllParamsClasses.xml`.

To remove it, you do the same thing, except you delete the template file and remove its entry from `AllParamsClasses.xml`.

##### 4.3.2.3. How to Modify an Existing Class Template

If you're modifying the parameters of a class, you can simply edit them in the respective template file - nothing else needs to be done.

If you are updating the name of a class, you need to update

 * the respective template file,
 * the *name* of the respective template file,
 * and its entry in `AllParamsClasses.xml`.

If you are updating the name of a *class category* (or creating a new one), you need to update/create

 * the respective directory,
 * the template files within the directory (should just be the parent node),
 * its entries in `AllParamsClasses.xml` and `BaseTemplateConfig.xml`,
 * and `BaseTemplateDefault.xml` (within `<ModelParams>`) to match.

Removing a class category is as similar process, though of course you delete files instead of creating them and remove entries instead of adding them.

#### 4.3.3. Workbench Directory Structure

Here is the current directory structure:

##### 4.3.3.1. Install Directory (Local)

This is where the actual executable/jar file is located. **This is read-only, and portable**. Thus, it can be put anywhere.

```
/usr/local/bin/Workbench/
├── lib/
└── GraphittiWorkbench.jar
```

##### 4.3.3.2. Workbench Directory (Local)

This is located in the user directory. It's the default location for `GraphittiRepo/`, and the place where the user info is stored (`user.josn`) along with some logs.

```
.workbench/
├── GraphittiRepo/
├── logs/
│   ├── provOverhead.txt
│   ├── WD-log.0
│   └── WD-WorkbenchManager-log.0
└── user.json
```

##### 4.3.3.3. Projects Directory (Local)

This is where the projects and the simulations for each project is stored. It's located in the user directory.

 * `MyProject/` - project directory.
	 * `testSim/` - simulation directory (there can be multiple of these, with different names, e.g., `anotherSim/`).
		 * `configfiles/` - this contains the XML configuration file for the simulator, along with the NList files for the active, inhibitory and probed neurons.
		 * `provenance/` - this contains the provenance (`.ttl` - turtle) file for the simulation.
		 * `script/` - this contains the script that was used to run this experiment, along with a log of what the script did (`testSim_scriptStatus.txt`), the SHA1 checkout key (i.e., the version of the source code for Graphitti used - `testSim_SHA1Key.txt`), and the output from the simulator (`testSim_simStatus.txt`).
	 * `MyProject.json` - this contains information about the project and all the simulations associated with it.
	 * `MyProjectProvenance.ttl` - this is the universal provenance file containing the provenance for all the simulations included in *MyProject*.

```
WorkbenchProjects/
├── Default/
│   └── Default.json
└── MyProject/
    ├── .artifacts/
    ├── testSim/
    │   ├── configfiles/
    │   │   ├── NList/
    │   │   │   ├── act.xml
    │   │   │   ├── inh.xml
    │   │   │   └── prb.xml
    │   │   └── testSim.xml
    │   ├── provenance/
    │   │   └── testSim.ttl
    │   └── script/
    │       ├── testSim_script.sh
    │       ├── testSim_scriptStatus.txt
    │       ├── testSim_SHA1Key.txt
    │       └── testSim_simStatus.txt
    ├── anotherSim/
    ├── MyProject.json
    └── MyProjectProvenance.ttl
```

##### 4.3.3.4. Simulations Directory (Local OR Remote)

This is the directory you will see on the remote machine (or the local one if you're running simulations locally - which is currently broken at the time this is being written). This is located in the user directory.

`Output/` and `RuntimeFiles/` and everything within them are from Graphitti and required by the exeuctable (`cgraphitti`) to run. That's all you need to know about them - they're **not** created by Workbench.

```
WorkbenchSimulations/
├── testSim/
│   ├── bin/   
│   │   ├── cgraphitti
│   ├── configfiles/
│   │   ├── NList/
│   │   │   ├── act.xml
│   │   │   ├── inh.xml
│   │   │   └── prb.xml
│   │   └── testSim.xml
│   ├── Output/
│   │   ├── Debug/
│   │   │   ├── edges.txt
│   │   │   ├── logging.txt
│   │   │   ├── README.md
│   │   │   └── vertices.txt
│   │   └── Results/
│   │       └── README.md
│   ├── results/
│   │   └── testSim-out.xml 
│   ├── RuntimeFiles/
│   │   ├── Data/
│   │   │   └── MersenneTwister.dat
│   │   └── log4cplus_configure.ini
│   ├── testSim_script.sh
│   ├── testSim_cmdOutput.txt
│   ├── testSim_scriptStatus.txt
│   ├── testSim_SHA1Key.txt
│   └── testSim_simStatus.txt
└── anotherSim/
```