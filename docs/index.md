[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.4678834.svg)](https://doi.org/10.5281/zenodo.4678834)

## About BrainGrid Workbench

The idea behind the BrainGrid Workbench is to develop an application to ease creating simulations, managing simulation artifacts, and analyzing simulation results. This is done by using graphical user interfaces, recording provenance and visualizations.

## Table of Contents

1. [Getting Started](workbench_getting_started.md)

   1.1. Clone the repository from GitHub
   
   1.2. Compile and build
   
       1.2.1. Maven

       1.2.2. NetBeans

2. [Running Simulations](workbench_running_simulations.md)

   2.1. The workflow
   
   2.2. About the generated script file

3. [Workbench Dashboard](workbench_dashboard.md)

   3.1. Running Workbench Dashboard
       
       3.1.1. Maven

       3.1.2. Java IDEs

   3.2. Visualizations and interactions

       3.2.1. Visualization

       3.2.2. Show a legend

       3.2.3. Show labels

       3.2.4. Move nodes around

       3.2.5. Highlighting an activity node and its related nodes

       3.2.6. Comparing two artifacts

   3.3. Technologies and third party libraries

4. [Internal Development](workbench_internal_development.md)

   4.1. Workbench Style Guide

       4.1.1. Java

       4.1.2. XML

   4.2. Tools and Plugins

       4.2.1. Apache Maven

       4.2.2. Checkstyle

    4.3. Implementation Details

        4.3.1. ProVis Node Spacing

            4.3.1.1. Current Behavior

            4.3.1.2. Expected Behavior

            4.3.1.3. Jupyter Notebook
        
        4.3.2. Programmer Workflow for Adding/Modifying Template Classes for Graphitti

            4.3.2.1. Description of all the Template Files

            4.3.2.2. How to Add/Remove a New Class Template

            4.3.2.3. How to Modify an Existing Class Template

        4.3.3. Workbench Directory Structure

            4.3.3.1. Install Directory (Local)

            4.3.3.2. Workbench Directory (Local)

            4.3.3.3. Projects Directory (Local)
			
			4.3.3.4. Simulations Directory (Local OR Remote)