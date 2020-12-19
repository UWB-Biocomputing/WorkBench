#!/bin/bash
# script created on: Thu Dec 17 20:54:20 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/thingie17_v1_scriptStatus.txt 2> ~/thingie17_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "simInputs: thingie17.xml\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/thingie17_v1_SHA1Key.txt 2>> /home/tori/thingie17_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
make "-s" "clean" >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mv -f /home/tori/thingie17.xml /home/tori/BrainGrid/workbenchconfigfiles/thingie17.xml\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mv -f /home/tori/thingie17.xml /home/tori/BrainGrid/workbenchconfigfiles/thingie17.xml >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mv -f /home/tori/active11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active11.xml\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mv -f /home/tori/active11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active11.xml >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mv -f /home/tori/probed11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed11.xml\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mv -f /home/tori/probed11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed11.xml >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inhib11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib11.xml\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
mv -f /home/tori/inhib11.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib11.xml >> ~/thingie17_v1_output.txt 2>> ~/thingie17_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/thingie17.xml\ntime started: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/thingie17.xml >> /home/tori/thingie17_v1_simStatus.txt 2>> /home/tori/thingie17_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/thingie17_v1_scriptStatus.txt 2>> ~/thingie17_v1_scriptStatus.txt
