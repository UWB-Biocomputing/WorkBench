package edu.uwb.braingrid.workbench.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.utils.DateTime;

/**
 * Analyzes script output files. Maintains executed statements and simulation execution parameters.
 *
 * Created by Nathan on 8/27/2014. Modified by Del on 9/3/2014.
 */
public class OutputAnalyzer {

    private static final int MILLISECONDS_PER_SECOND = 1000;

    private final HashMap<String, HashMap<String, ExecutedCommand>> commandsRun;

    /**
     * Responsible for allocating this analyzer and initializing all members.
     */
    public OutputAnalyzer() {
        commandsRun = new HashMap<>();
    }

    /**
     * Analyzes an output file. The output file is the redirected standard output of printf commands
     * that provide information about commands that are executed in a script.
     *
     * @param filepath  The path to a file to analyze
     */
    public void analyzeOutput(String filepath) {
        Scanner fileReader = null;
        /* Stage Error Handling */
        try { // try to start reading from the given file path
            fileReader = new Scanner(new FileReader(new File(filepath)));
        } catch (FileNotFoundException e) {
            System.err.println("Script Output Analyzer could not find " + filepath);
        }
        // given file path does not exist
        if (fileReader == null) {
            return;
        }
        /* Analyze */
        // iterate through the file
        while (fileReader.hasNext()) {
            String currentLine = fileReader.nextLine();
            // this line has the command text, start processing a new command
            if (currentLine.startsWith(Script.COMMAND_TEXT)) {
                // get the command from the line
                currentLine = currentLine.substring(currentLine.indexOf(":") + 1).trim();
                ExecutedCommand ec;
                // check if already in the hashmap, if not create new object
                ec = getExecutedCommand(currentLine);
                // get time started date object from the date in the string
                Date startedDate;
                String startDate = getImportantText(fileReader, Script.START_TIME_TEXT);
                if (startDate != null) {
                    startedDate = new Date(Long.parseLong(startDate) * MILLISECONDS_PER_SECOND);
                    ec.setTimeStarted(startedDate); // set the start time
                }
                // get the exit status now
                String nextLine = getImportantText(fileReader, Script.EXIT_STATUS_TEXT);
                // set the exit status
                if (nextLine != null) {
                    ec.setExitStatus(Integer.parseInt(nextLine));
                }
                // get the date object from the date in the string (time ended)
                Date endDate = null;
                String completedDateText = getImportantText(fileReader, Script.COMPLETED_TIME_TEXT);
                if (completedDateText != null) {
                    endDate = new Date(Long.parseLong(completedDateText) * MILLISECONDS_PER_SECOND);
                }
                ec.setTimeCompleted(endDate); // set the end time
                addCommand(ec);
            }
        }
        fileReader.close();
    }

    /**
     * Retrieves an executed command datum based on the full command that was executed.
     *
     * @param fullCommand  A key value used to look up the associated executed command datum. This
     *                     should precisely match the executable name followed by any arguments.
     *                     Additionally, it may contain multiple fullCommands, if several commands
     *                     were executed in sequence on the same command line invocation
     * @return The executed command associated with the full command provided or null
     */
    public ExecutedCommand getExecutedCommand(String fullCommand) {
        ExecutedCommand ec = null;
        HashMap<String, ExecutedCommand> map = null;
        if (fullCommand != null) {
            String simpleCommand = ExecutedCommand.fullToSimpleCommand(fullCommand);
            map = commandsRun.get(simpleCommand);
        }
        if (map != null) {
            ec = map.get(fullCommand);
        }
        if (ec == null) {
            ec = new ExecutedCommand(fullCommand);
        }
        return ec;
    }

    /**
     * Takes the next line of text from the file and gets the contents after the colon.
     *
     * @param fileReader  Scanner whose position in the stream is arbitrary
     * @param expectedDescriptor  A prefix expected to be found at the beginning of the next line of
     *                            text
     * @return The contents of the next line after the colon
     */
    private String getImportantText(Scanner fileReader, String expectedDescriptor) {
        String textToReturn = null;
        // the file has another line
        if (fileReader.hasNext()) {
            textToReturn = fileReader.nextLine(); // get the next line
            // has the correct line descriptor, parse the line
            if (textToReturn.trim().startsWith(expectedDescriptor)) {
                textToReturn = textToReturn.substring(textToReturn.indexOf(":") + 1).trim();
            } else {
                // error for future debugging
                System.err.println("Warning: did not find the correct line descriptor when"
                        + "analyzing the output file. See " + this.getClass().getName()
                        + ".java. Expected: " + expectedDescriptor);
                textToReturn = null;
            }
        }
        return textToReturn;
    }

    /**
     * Provides a collection of commands invoked from the provided executable file.
     *
     * @param executableName  The executable filename that starts the commands. This may include
     *                        system-dependent scoping (e.g. ./executableName on Unix-based systems)
     * @return A collection of commands
     */
    public Collection<ExecutedCommand> getCollectionByExecName(String executableName) {
        Collection<ExecutedCommand> commands = null;
        String exec = FileManager.getBaseFilename(executableName);
        HashMap<String, ExecutedCommand> map = commandsRun.get(exec);
        if (map != null) {
            commands = map.values();
        }
        return commands;
    }

    /**
     * Provides all of the commands found during analysis.
     *
     * @return  All of the commands found during analysis
     */
    public Collection<ExecutedCommand> getAllCommands() {
        HashMap<String, ExecutedCommand> map = new HashMap<>();
        for (Entry<String, HashMap<String, ExecutedCommand>> i : commandsRun.entrySet()) {
            for (Entry<String, ExecutedCommand> j : i.getValue().entrySet()) {
                map.put(j.getValue().getFullCommand(), j.getValue());
            }
        }
        return map.values();
    }

    private void addCommand(ExecutedCommand ec) {
        if (commandsRun.containsKey(ec.getSimpleCommand())) {
            commandsRun.get(ec.getSimpleCommand()).put(ec.getFullCommand(), ec);
        } else {
            HashMap<String, ExecutedCommand> map = new HashMap<>();
            map.put(ec.getFullCommand(), ec);
            commandsRun.put(ec.getSimpleCommand(), map);
        }
    }

    /**
     * Provides the time, in milliseconds since January 1, 1970, 00:00:00 GMT, that the first
     * executed command with the specified type finished execution.
     *
     * @param executableName  Executable filename (such as make, ./growth, git, etc.)
     * @return The time that the command completed or an error code indicating that the script had
     *         not completed at the time when was copied or downloaded to the project script
     *         directory
     */
    public long completedAt(String executableName) {
        Date date;
        long time = DateTime.ERROR_TIME;
        Collection<ExecutedCommand> commands = getCollectionByExecName(executableName);
        if (commands != null) {
            for (ExecutedCommand ec : commands) {
                if (ec.hasCompleted()) {
                    date = ec.getTimeCompleted();
                    if (date != null) {
                        time = ec.getTimeCompleted().getTime();
                    }
                    break;
                }
            }
        }
        return time;
    }

    /**
     * Provides the time, in milliseconds since January 1, 1970, 00:00:00 GMT, that the first
     * executed command with the specified type started execution.
     *
     * @param executableName  Executable filename (such as make, ./growth, git, etc.)
     * @return The time that the command completed or an error code indicating that the script had
     *         not completed at the time when was copied or downloaded to the project script
     *         directory
     */
    public long startedAt(String executableName) {
        Date date;
        long time = DateTime.ERROR_TIME;
        Collection<ExecutedCommand> commands = getCollectionByExecName(executableName);
        if (commands != null) {
            for (ExecutedCommand ec : commands) {
                if (ec.hasCompleted()) {
                    date = ec.getTimeCompleted();
                    if (date != null) {
                        time = ec.getTimeStarted().getTime();
                    }
                    break;
                }
            }
        }
        return time;
    }

    /**
     * Provides the first executed command with the specified executable filename from all recorded
     * commands. There is no guaranteed ordering of executed commands. This method should only be
     * used when it doesn't matter which of the matching commands is returned, or when the
     * executable filename is only invoked once.
     *
     * @param executableName  The executable name of the executed command
     * @return The first executed command with the given executable name
     */
    public ExecutedCommand getFirstCommand(String executableName) {
        ExecutedCommand command = null;
        Collection<ExecutedCommand> commands = getCollectionByExecName(executableName);
        if (commands != null) {
            for (ExecutedCommand ec : commands) {
                command = ec;
                break;
            }
        }
        return command;
    }
}
