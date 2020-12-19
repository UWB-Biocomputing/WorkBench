#!/bin/bash
# script created on: Thu Dec 17 19:24:47 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/gftest1_v1_scriptStatus.txt 2> ~/gftest1_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "simInputs: gftest1.xml\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: git clone https://github.com/UWB-Biocomputing/BrainGrid.git /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
git "clone" "https://github.com/UWB-Biocomputing/BrainGrid.git" "/home/tori/BrainGrid" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: git pull\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
git "pull" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: git checkout b9021a1 \ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
git "checkout" "b9021a1 " >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/gftest1_v1_SHA1Key.txt 2>> /home/tori/gftest1_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
make "-s" "clean" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mv -f /home/tori/gftest1.xml /home/tori/BrainGrid/workbenchconfigfiles/gftest1.xml\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mv -f /home/tori/gftest1.xml /home/tori/BrainGrid/workbenchconfigfiles/gftest1.xml >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml >> ~/gftest1_v1_output.txt 2>> ~/gftest1_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/gftest1.xml\ntime started: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/gftest1.xml >> /home/tori/gftest1_v1_simStatus.txt 2>> /home/tori/gftest1_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/gftest1_v1_scriptStatus.txt 2>> ~/gftest1_v1_scriptStatus.txt
