#!/bin/bash
# script created on: Thu Dec 17 21:03:49 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/beepboop12_v1_scriptStatus.txt 2> ~/beepboop12_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "simInputs: beepboop12.xml\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/beepboop12_v1_SHA1Key.txt 2>> /home/tori/beepboop12_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
make "-s" "clean" >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mv -f /home/tori/beepboop12.xml /home/tori/BrainGrid/workbenchconfigfiles/beepboop12.xml\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mv -f /home/tori/beepboop12.xml /home/tori/BrainGrid/workbenchconfigfiles/beepboop12.xml >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mv -f /home/tori/pbd2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd2.xml\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mv -f /home/tori/pbd2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd2.xml >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mv -f /home/tori/act2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act2.xml\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mv -f /home/tori/act2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act2.xml >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inh2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh2.xml\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
mv -f /home/tori/inh2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh2.xml >> ~/beepboop12_v1_output.txt 2>> ~/beepboop12_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/beepboop12.xml\ntime started: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/beepboop12.xml >> /home/tori/beepboop12_v1_simStatus.txt 2>> /home/tori/beepboop12_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/beepboop12_v1_scriptStatus.txt 2>> ~/beepboop12_v1_scriptStatus.txt
