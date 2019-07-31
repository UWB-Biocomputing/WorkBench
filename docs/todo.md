WorkBench bugs/unsatisfactory things: (can be added to)

- Text on dialogue popups is extremely small, with no way to resize
- Main window does not respond and goes opaque 
	when a popup is hidden and still active, which may
	confuse the user into thinking the program has frozen
- Welcome! tab is empty 
- Simulation configuration dialogue box default size hides the buttons on the bottom 
- Script Specification Dialogue box only lets you press 'OK' if 'Simulator Folder Location'
	has been edited, even if it has already been filled in
- User is able to type in console
- ProvVis is not implemented
- Repo -> Update Main provides no feedback to the user that the repo was actually updated
- Specify Script and Generate Script can be collapsed into one
	- Instead of 'OK' in specify script dialogue box, could be 'Generate' to generate
	  the shell script
- User has to respecify and regenerate script if there is an error with running the script
- Application crashes when user focuses out of a currently active dialogue box
- File -> Open from main window does not elicit a dialogue box
- File -> Open from simstarter tab elicits a dialogue box, but does not do anything once 
	a file is selected
- File -> Recent Projects does not elicit a dialogue box
- Creating a new sim starter with the same name as an existing project does not warn the 
	user about any potential overwrites. 	
- Configure button becomes unresponsive if there is an IOexception when trying to open 
	an existing xml file. You need to then close the tab and make a new simstarter.
- Test Connection on remote server is finnicky (sometimes connection successful, sometimes
	connection failed)
- Have to retype remote location password every time you bring up the Script Specification dialogue box
- Application freezes during simulation/analysis on remote machine which may confuse the user into
	thinking it crashed

Workflow:
- I tested a small 5x5 neuron layout with 2 active, 2 inhibitory, and 2 probed neurons
  on a remote server (Raiju.uwb.edu) (Simulator Location: ~/BrainGrid, Build option: build, execution type: sequential,
		source code updating: None/pull) 
			
	- everything seems to go as planned -- it sends all the info to the remote machine
		and runs the script on there, sends results back
	- however, the results I've gotten indicate that the machine cannot find growth:
		OUTPUT "Latest output from simulation:
			./test_script2.sh: line 56: ./growth: No such file or directory" 
Yet to be tested:
  - Local simulator location
There are possibly many more things to be tested, these are just some I've looked at.
