#!/bin/bash
# script created on: Wed Dec 16 22:16:36 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/pktest3_v1_scriptStatus.txt 2> ~/pktest3_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "simInputs: pktest3.xml\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/pktest3_v1_SHA1Key.txt 2>> /home/tori/pktest3_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
make "-s" "clean" >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mv -f /home/tori/pktest3.xml /home/tori/BrainGrid/workbenchconfigfiles/pktest3.xml\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mv -f /home/tori/pktest3.xml /home/tori/BrainGrid/workbenchconfigfiles/pktest3.xml >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml >> ~/pktest3_v1_output.txt 2>> ~/pktest3_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/pktest3.xml\ntime started: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/pktest3.xml >> /home/tori/pktest3_v1_simStatus.txt 2>> /home/tori/pktest3_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/pktest3_v1_scriptStatus.txt 2>> ~/pktest3_v1_scriptStatus.txt
