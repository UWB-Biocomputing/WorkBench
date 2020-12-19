#!/bin/bash
# script created on: Fri Dec 18 20:14:45 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/practice_v1_scriptStatus.txt 2> ~/practice_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "simInputs: practice.xml\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/practice_v1_SHA1Key.txt 2>> /home/tori/practice_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mv -f /home/tori/practice.xml /home/tori/BrainGrid/workbenchconfigfiles/practice.xml\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mv -f /home/tori/practice.xml /home/tori/BrainGrid/workbenchconfigfiles/practice.xml >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mv -f /home/tori/probed.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/probed.xml >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mv -f /home/tori/inhib.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inhib.xml >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
mv -f /home/tori/active.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/active.xml >> ~/practice_v1_output.txt 2>> ~/practice_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/practice.xml\ntime started: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/practice.xml >> /home/tori/practice_v1_simStatus.txt 2>> /home/tori/practice_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/practice_v1_scriptStatus.txt 2>> ~/practice_v1_scriptStatus.txt
