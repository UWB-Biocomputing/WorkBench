[![DOI](https://zenodo.org/badge/6034062.svg)](https://zenodo.org/badge/latestdoi/6034062)
## About BrainGrid

The idea behind the BrainGrid Project is to develop a toolkit/software architecture to ease creating **high-performance neural network simulators**. It is particularly focused on facilitating biologically realistic modeling. 

## More about BrainGrid? 
Visit our website at: http://uwb-biocomputing.github.io/BrainGrid/

## INSTALLATION
I. TOOL LIBRARIES
List of required software and libraries:
1. NAME: Ubuntu Linux 18.04
2. URL: If installing the Windows Subsystem for Linux (WSL2), no URL is available. Installation is performed through the OS.
3. INSTALL TEST: Start the Ubuntu application, then issue the command '$ lsb_release -a'

4. INSTALL PROCEDURE: Due to the regular updates applied to the Windows 10 OS, the official installation procedure should be referenced when attempting to install the WSL2. These instructions are available at the following location:
Install Windows Subsystem for Linux (WSL) on Windows 10

1. NAME: Git - Version control application
2. URL: If installing to Linux, no URL is available. Installation is performed through the apt-get utility.
3. INSTALL TEST: In Ubuntu, issue the command '$ git --version'

4. INSTALL PROCEDURE: Install Git to Linux using the command '$ sudo apt-get install git'

1. NAME: Maven
2. URL: If installing to Linux, no URL is available. Installation is performed through the apt-get utility.
3. INSTALL TEST: In Ubuntu, issue the command '$ mvn --version'

4. INSTALL PROCEDURE: Install Maven to Linux using the command '$ sudo apt install maven'

1. NAME: JDK 1.8.0.242
2. URL: See Appendix - Tool Installation for resource locations
3. INSTALL TEST: See Appendix - Tool Installation for installation test
4. INSTALL PROCEDURE: See Appendix - Tool Installation for installation procedure

I. Tool Installation
INSTALL JAVA DEV KIT (JDK) 1.8
From the Java Archive Downloads - Java SE 8, download the appropriate TAR archive (also GNU zipped).

Accept the user agreement, then sign-in or create an account to access the file.

After successful login, the download should begin.

After the download is complete, create a directory to extract the TAR archive into
$ sudo mkdir -p /usr/lib/jvm/jdk-8

Navigate to the local Downloads folder containing the TAR archive.
$ <COMMANDS VARY PER SYSTEM>

Unpack the archive
$ tar xvf jdk-8u144-linux-x64.tar.gz

WARNING!
Improper installation of the Java archive (or any archive) can result in irreparable damage to the Linux OS. Be absolutely sure and extremely careful of what the effects of the following commands are in your environment before executing them. Note that certain commands will have different effects based upon where the working directory is located when the command is executed.

Ideally, if you are evaluating this software, perform the installation and evaluation in a container or OS image such that if damage occurs, it is compartmentalized.

Validate the unpacking of the archive and the JDK version therein
$ cd jdk1.8.0_144/
$ ./bin/java -version

Move the unpacked folder to the /usr/lib/ directory, then move to that directory
$ cd ..
$ sudo mv jdk1.8.0_144/ /usr/lib/
$ cd /usr/lib

Install the JDK using the commands:
$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jdk1.8.0_144/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jdk1.8.0_144/bin/javac 1

Validate installation version with:
$ java -version

DOWNLOAD BRAINGRID/WORKBENCH REPOSITORY
Navigate to the location that the local Braingrid installation should reside in.

Pull the Git repositories using the commands:
$ git clone https://github.com/UWB-Biocomputing/BrainGrid.git
$ git clone https://github.com/UWB-Biocomputing/WorkBench.git

BUILD THE WORKBENCH REPOSITORY
Navigate to the pulled WorkBench repository. Observe the 'pom.xml' file (used by Maven to compile Java files)

Note that Workbench has outstanding compatibility issues with Java versions that are not 1.8.0_144
Compile a new build with:
$ mvn install

Switch to Java 1.8.0_144 for Maven by: 
$ sudo update-alternatives --config java
CONFIGURE THE BRAINGRID/WORKBENCH SIMULATOR
Run BrainGrid by navigating to the target directory, then using the command:
$ cd target/
$ java -jar BrainGridWorkbench-1.0-SNAPSHOT.jar

Observe results of build:

Note: Provenance files go into the target folder
Note: The target folder will be purged on a 'clean install'

Navigate to WorkbenchProject/target and execute:
$ java -jar BrainGridWorkbench-1.0-SNAPSHOT.jar

The Provis visualization interface will generate and open

Go to File -> Growth Simulation Layout Editor

Use the radio buttons and interface to configure a start neural state

Export the neuron state using the 'Export' button:

Upon save, the layout files are saved to the Target folder.

Go to File -> Simulation Starter and provide a name for the simulation.

Set the configuration options for the simulation.

Build may be executed at this point, but will not operate properly without a layout file. Import layout parameters by going to the 'LayoutParams' tab and selecting the neural layouts defined previously.

RUN THE BRAINGRID/WORKBENCH SIMULATOR
https://drive.google.com/file/d/1YSyrVa71hu0P3qJGjQxc-8gbvYESsyB2/view?usp=sharing

Return to the layoutParams tab and load the appropriate .XML configuration files to the active, inhibitory, and probing neuron file locations.

In order to run Braingrid, it must be done on a Linux machine.

Prior to the noted refactoring, the team met with the individual closest to Workbench who demonstrated this running with the remote machine. They were interfacing with the simulator through this process. 
Install BigIP, the UW VPN client?
Log in to lab computer

 add dash -> 

No feedback on progress of simulation

How To:
Video capture and edit:
Download Open Broadcaster Software (OBS) - https://obsproject.com/

Run the installer

OBS Studio (OBSS) will activate automatically.
Select appropriate configuration preset from the options provided.

Add a video source using the 'Source' control window

You may choose an audio input. With the source window (and audio) selected, press 'Start Recording' in OBSS. 

Perform the actions you wish to record. You may narrate as you perform the actions. Press 'Stop Recording' when you are done.
Go to File -> Show Recordings to see the recordings you have created.

Editing clips together
Using Lightworks
Download the Lightworks installer from https://www.lwks.com/

On startup, you may be asked to register for a lightworks account to continue using the free distribution.

Select 'Create a New Project'

Note: The selected frame rate must match the target video clips for import (Zoom uses 25 fps)

Drag-and-drop from a file browser into the Lightworks 'Clips' workspace to add a file as a clip

Go to the Edit tab to open the timeline for editing.

Drag-and-drop the clip from the 'Clips' window over to the timeline on the bottom.

Position and trim clips until satisfactory. To export, right click on the timeline and select 'Export' -> H.264/MP4

Workbench setup:
Make a working directory (in this case 'braingrid')
Navigate into the working directory.

Clone repository using
$ git clone https://github.com/UWB-Biocomputing/WorkBench.git

Validate clone by listing ('ls') the folder contents

Navigate into the WorkbenchProject folder and observe the 'pom.xml' file (used by Maven to compile Java files)
Validate Maven installation with
$ mvn -v

If Maven is not installed, install it using
$ sudo apt install maven

Note that Workbench has outstanding compatibility issues with Java versions that are not 1.8.0_111
Navigate to the location of the POM file and compile a new build with:
$ mvn clean install

Observe results of build:

Note: Provenance files go into the target folder
Note: The target folder will be purged on a 'clean install'

Navigate to WorkbenchProject/target and execute:
$ java -jar BrainGridWorkbench-1.0-SNAPSHOT.jar

The Provis visualization interface will generate and open

Go to File -> Simulation Starter

Build may be executed at this point, but will not operate properly without a layout file.

Import layout parameters by going to the 'LayoutParams' tab (by default, no layout parameter files will be bundled with the workbench)

Set start layout state

Upon save, the layout files are saved to the Target folder

Return to the layoutParams tab and load the appropriate .XML configuration files to the active, inhibitory, and probing neuron file locations.

In order to run Braingrid, it must be done on a Linux machine

Install BigIP (UW VPN client?)
Log in to lab computer

 add dash -> 

No feedback on progress of simulation