#!/bin/bash
# script created on: Fri Dec 18 15:43:22 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/attemptA_v1_scriptStatus.txt 2> ~/attemptA_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "simInputs: attemptA.xml\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: git clone https://github.com/UWB-Biocomputing/BrainGrid.git /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
git "clone" "https://github.com/UWB-Biocomputing/BrainGrid.git" "/home/tori/BrainGrid" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: git pull\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
git "pull" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/attemptA_v1_SHA1Key.txt 2>> /home/tori/attemptA_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
make "-s" "clean" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mv -f /home/tori/attemptA.xml /home/tori/BrainGrid/workbenchconfigfiles/attemptA.xml\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mv -f /home/tori/attemptA.xml /home/tori/BrainGrid/workbenchconfigfiles/attemptA.xml >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mv -f /home/tori/act_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act_B.xml\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mv -f /home/tori/act_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act_B.xml >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mv -f /home/tori/pbd_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd_B.xml\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mv -f /home/tori/pbd_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd_B.xml >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inh_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh_B.xml\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
mv -f /home/tori/inh_B.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh_B.xml >> ~/attemptA_v1_output.txt 2>> ~/attemptA_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/attemptA.xml\ntime started: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/attemptA.xml >> /home/tori/attemptA_v1_simStatus.txt 2>> /home/tori/attemptA_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptA_v1_scriptStatus.txt 2>> ~/attemptA_v1_scriptStatus.txt
