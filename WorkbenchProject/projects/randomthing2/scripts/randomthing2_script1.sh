#!/bin/bash
# script created on: Thu Dec 17 20:18:51 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/randomthing2_v1_scriptStatus.txt 2> ~/randomthing2_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "simInputs: randomthing2.xml\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/randomthing2_v1_SHA1Key.txt 2>> /home/tori/randomthing2_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: make -s clean\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
make "-s" "clean" >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: make -s growth CUSEHDF5='no'\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
make "-s" "growth" "CUSEHDF5='no'" >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mv -f /home/tori/randomthing2.xml /home/tori/BrainGrid/workbenchconfigfiles/randomthing2.xml\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mv -f /home/tori/randomthing2.xml /home/tori/BrainGrid/workbenchconfigfiles/randomthing2.xml >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mv -f /home/tori/pbd2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd2.xml\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mv -f /home/tori/pbd2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd2.xml >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mv -f /home/tori/act2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act2.xml\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mv -f /home/tori/act2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act2.xml >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inh2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh2.xml\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
mv -f /home/tori/inh2.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh2.xml >> ~/randomthing2_v1_output.txt 2>> ~/randomthing2_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/randomthing2.xml\ntime started: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/randomthing2.xml >> /home/tori/randomthing2_v1_simStatus.txt 2>> /home/tori/randomthing2_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/randomthing2_v1_scriptStatus.txt 2>> ~/randomthing2_v1_scriptStatus.txt
