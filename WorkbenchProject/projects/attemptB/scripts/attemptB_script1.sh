#!/bin/bash
# script created on: Fri Dec 18 15:53:33 PST 2020


if [ "$#" -ne 0 ]; then
	echo "wrong number of arguments. expected 0"
	echo "usage:  ${0##*/}"
exit 1
fi

printf "version: 1\n" > ~/attemptB_v1_scriptStatus.txt 2> ~/attemptB_v1_scriptStatus.txt
printf "simExecutable: growth\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "simInputs: attemptB.xml\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "simOutputs: output.xml\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "endSimSpec: \n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mkdir "-p" "/home/tori/BrainGrid" >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: cd /home/tori/BrainGrid\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
cd "/home/tori/BrainGrid" >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: git log --pretty=format:'%%H' -n 1\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
git log --pretty=format:'%H' -n 1 > /home/tori/attemptB_v1_SHA1Key.txt 2>> /home/tori/attemptB_v1_SHA1Key.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mkdir /home/tori/BrainGrid/results\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mkdir "/home/tori/BrainGrid/results" >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mkdir -p /home/tori/BrainGrid/workbenchconfigfiles/NList >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mv -f /home/tori/attemptB.xml /home/tori/BrainGrid/workbenchconfigfiles/attemptB.xml\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mv -f /home/tori/attemptB.xml /home/tori/BrainGrid/workbenchconfigfiles/attemptB.xml >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mv -f /home/tori/pbd_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd_Bmore.xml\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mv -f /home/tori/pbd_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/pbd_Bmore.xml >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mv -f /home/tori/inh_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh_Bmore.xml\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mv -f /home/tori/inh_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/inh_Bmore.xml >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: mv -f /home/tori/act_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act_Bmore.xml\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
mv -f /home/tori/act_Bmore.xml /home/tori/BrainGrid/workbenchconfigfiles/NList/act_Bmore.xml >> ~/attemptB_v1_output.txt 2>> ~/attemptB_v1_output.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
printf "command: ./growth -t workbenchconfigfiles/attemptB.xml\ntime started: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
./growth -t workbenchconfigfiles/attemptB.xml >> /home/tori/attemptB_v1_simStatus.txt 2>> /home/tori/attemptB_v1_simStatus.txt
printf "exit status: $?\ntime completed: `date +%s`\n" >> ~/attemptB_v1_scriptStatus.txt 2>> ~/attemptB_v1_scriptStatus.txt
