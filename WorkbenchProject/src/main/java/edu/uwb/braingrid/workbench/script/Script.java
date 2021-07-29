package edu.uwb.braingrid.workbench.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Provides support for building simple bash scripts in Java, where it is anticipated that the
 * script will automatically record provenance and execution output to files. Provenance support
 * includes the time that commands began execution, ended execution, and their state when execution
 * completed.
 *
 * Note: Variable names and paths diverge to enable Windows batch support in future releases.
 * Please, do not refactor the code away from this support.
 *
 * Created by Nathan on 7/16/2014; Updated by Del on 7/23/2014 and maintained since.
 *
 * @author Nathan Duncan
 * @version 0.1
 * @since 0.1
 */
public class Script {

    // <editor-fold defaultstate="collapsed" desc="Members">
    /** Prefix text for the echo of a command. */
    public static final String COMMAND_TEXT = "command";
    /** Prefix text for milliseconds since epoch when a command was executed. */
    public static final String START_TIME_TEXT = "time started";
    /** Prefix text for milliseconds since epoch when an executed command completed. */
    public static final String COMPLETED_TIME_TEXT = "time completed";
    /** Prefix text for the exit status of an executed command. */
    public static final String EXIT_STATUS_TEXT = "exit status";
    /** Prefix text for the version of the executed script. */
    public static final String VERSION_TEXT = "version";
    /** Redirect file for std-err/std-out on printf statements. */
    public static final String SCRIPT_STATUS_FILENAME = "scriptStatus.txt";
    /** Redirect file for std-err and std-out of executed commands. */
    public static final String COMMAND_OUTPUT_FILENAME = "output.txt";
    /** Redirect file for git commit key. */
    public static final String SHA1_KEY_FILENAME = "SHA1Key.txt";
    /** Redirect file for simulation status. */
    public static final String SIM_STATUS_FILENAME = "simStatus.txt";
    /** Default directory for script output files. */
    public static final String DEFAULT_SCRIPT_OUTPUT_DIRECTORY = "$HOME";

    /* script variable names and reference strings */
    private static final String SCRIPT_STATUS_VAR = "scriptStatus";
    private static final String SCRIPT_STATUS_REF = "\"$scriptStatus\"";
    private static final String CMD_OUTPUT_VAR = "cmdOutput";
    private static final String CMD_OUTPUT_REF = "\"$cmdOutput\"";

    /* model data */
    private String bashScript;
    private final List<String> bashStatements;
    private final List<String> bashArgNames;
    private final List<String> bashArgDeclarations;
    private final List<String> usageStatements;
    /* persist validation flag */
    private boolean bashScriptConstructed;
    /* temporary variables */
    private StringBuilder sb;
    /* redirect stdout/stderr of commands to this file */
    private String cmdOutputFilename;
    /* redirect stdout/stderr of printf statements to this file */
    private String scriptStatusOutputFilename;
    /* create all script output files in this directory */
    private String scriptOutputDirectory;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Constructs Script object and initializes members.
     */
    public Script() {
        bashScript = "";
        bashStatements = new ArrayList<>();
        bashArgNames = new ArrayList<>();
        bashArgDeclarations = new ArrayList<>();
        usageStatements = new ArrayList<>();
        bashScriptConstructed = false;
        cmdOutputFilename = COMMAND_OUTPUT_FILENAME;
        scriptStatusOutputFilename = SCRIPT_STATUS_FILENAME;
        scriptOutputDirectory = DEFAULT_SCRIPT_OUTPUT_DIRECTORY;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Data Manipulation">
    /**
     * Shortcut helper function, to be used when all args are not variable and thus there are no
     * corresponding usage statements.
     *
     * @param filename  Name of the file to execute from this script
     * @param args  Arguments to provide for execution. Even for variable arguments this is
     *              necessary because the names are used in the usage for the script.
     * @param fromWrkDir  Indicates whether or not the executable should be prefaced by the working
     *                    directory syntax (used to differentiate between pathed executables and
     *                    executables existing in the current working directory, but having the same
     *                    name e.g. ./exec_name)
     * @return True if the program execution statement was added to the script model correctly,
     *         otherwise false
     */
    public boolean executeProgram(String filename, String[] args, boolean fromWrkDir) {
        boolean[] variable = new boolean[args.length];
        String[] addToUsage = new String[args.length];
        for (int i = 0, im = args.length; i < im; i++) {
            variable[i] = false;
            addToUsage[i] = "";
        }
        return executeProgram(filename, args, variable, addToUsage, fromWrkDir);
    }

    /**
     * Executes a program without ./ such as cd or mkdir or ls, etc.
     *
     * @param filename  Name of the file to execute from this script
     * @param args  Arguments to provide for execution. Even for variable arguments this is
     *              necessary because the names are used in the usage for the script.
     * @return True if the program execution statement was added to the script model correctly,
     *         otherwise false
     */
    public boolean executeProgram(String filename, String[] args) {
        return executeProgram(filename, args, false);
    }

    /**
     * Executes a given program with the provided arguments. Some of the arguments may be variable.
     * Whether or not they are variable is determined by the boolean value at the corresponding
     * position in variable parameter. A description of each variable should be provided.
     *
     * Note: The length of args, variable, and addToUsage must be the same.
     *
     * @param filename  Name of the file to execute
     * @param args  Arguments to provide for execution. Even for variable arguments this is
     *              necessary because the names are used in the usage for the script.
     * @param variable  Whether or not each argument should be script variable
     * @param addToUsage  Explanation of each argument. (The usage explanation for non-variable args
     *                    will not be used, but must be specified or null as the length of
     *                    addToUsage must match the args and variable parameters)
     * @param fromWrkDir  Indicates whether or not the executable should be prefaced by the working
     *                    directory syntax (used to differentiate between pathed executables and
     *                    executables existing in the current working directory, but having the same
     *                    name e.g. ./exec_name) otherwise false
     * @return True if the statement was successfully appended to the current script state (does not
     *         indicate persistence), otherwise false
     */
    public boolean executeProgram(String filename, String[] args, boolean[] variable,
            String[] addToUsage, boolean fromWrkDir) {
        boolean success1;
        success1 = executeProgramForBash(filename, args, variable, addToUsage, fromWrkDir);
        return success1;
    }

    /**
     * Executes a given program with the provided arguments. Some of the arguments may be variable.
     * Whether or not they are variable is determined by the boolean value at the corresponding
     * position in variable parameter. A description of each variable should be provided.
     *
     * Note: The length of args, variable, and addToUsage must be the same.
     * Note: This does not execute a program. It only adds the execution command to the global
     * variable bashStatements.
     *
     * @param filename  Name of the file to execute
     * @param args  Arguments to provide for execution. Even for variable arguments this is
     *              necessary because the names are used in the usage for the script.
     * @param variable  Whether or not each argument should be script variable
     * @param addToUsage  Explanation of each argument. (The usage explanation for non-variable args
     *                    will not be used, but must be specified or null as the length of
     *                    addToUsage must match the args and variable parameters)
     * @param fromWrkDir  Indicates whether or not the executable should be prefaced by the working
     *                    directory syntax (used to differentiate between pathed executables and
     *                    executables existing in the current working directory, but having the same
     *                    name e.g. ./exec_name)
     * @return True if the statement was successfully appended to the current script state (does not
     *         indicate persistence), otherwise false
     */
    private boolean executeProgramForBash(String filename, String[] args, boolean[] variable,
            String[] addToUsage, boolean fromWrkDir) {
        String dotSlash = fromWrkDir ? "./" : "";
        boolean success = true;
        if (args.length == variable.length && args.length == addToUsage.length) {
            sb = new StringBuilder();
            sb.append(dotSlash).append(filename).append(' ');
            for (int i = 0, im = args.length; i < im; i++) {
                if (i != 0) {
                    sb.append(' ');
                }
                if (variable[i]) {
                    bashArgNames.add(args[i]);
                    bashArgDeclarations.add(new StringBuilder("arg")
                            .append(bashArgDeclarations.size() + 1)
                            .append("=$")
                            .append(bashArgDeclarations.size() + 1)
                            .toString());
                    usageStatements.add(addToUsage[i]);

                    sb.append("\"$arg").append(bashArgDeclarations.size()).append("\"");
                } else {
                    sb.append('\"').append(args[i]).append("\"");
                }
            }
            preCommandOutput(sb.toString(), !bashStatements.isEmpty());
            sb.append(" >");
            if (!bashStatements.isEmpty()) {
                sb.append('>');
            }
            sb.append(" ").append(CMD_OUTPUT_REF);
            sb.append(" 2>");
            if (!bashStatements.isEmpty()) {
                sb.append(">");
            }
            sb.append(" ").append(CMD_OUTPUT_REF);
            bashStatements.add(sb.toString());
            postCommandOutput(!bashStatements.isEmpty());
        } else {
            success = false;
        }
        return success;
    }

    /**
     * Adds a printf statement to the script. The argument to printf is composed of:
     * prefix: statement
     *
     * @param prefix  The prefix used to identify the statement during analysis
     * @param statement  The statement indicating the value associated with the prefix
     * @param append  True if the redirected standard out descriptor and standard error descriptor
     *                should be appended to, False if they should be overwritten by the output of
     *                this printf statement
     */
    public void printf(String prefix, String statement, boolean append) {
        String escapedStatement = printfEscape(statement);
        String outToken = append ? ">>" : ">";
        String s = "printf \"" + prefix + ": " + escapedStatement + "\\n\" " + outToken + " "
                + SCRIPT_STATUS_REF + " 2" + outToken + " " + SCRIPT_STATUS_REF;
        bashStatements.add(s);
    }

    /**
     * Adds a command statement to the script irrespective of script variables.
     *
     * Note: Since any file name, previously used or not, can be specified as the output file, this
     * function has no safe-guards against overwriting previous redirected output. If the file has
     * been previously used in the script, but isn't appended to in future uses, it will be
     * overwritten. On the other hand, if the file is used as a redirect for the first time in the
     * script and it is appended to, the file may contain output from previous execution of the
     * script.
     *
     * @param stmt  Command statement to be added
     * @param outputFile  File to redirect standard error/out to
     * @param append  Whether or not the output file should be appended to
     */
    public void addVerbatimStatement(String stmt, String outputFile, boolean append) {
        preCommandOutput(stmt, !bashStatements.isEmpty());
        String redirectString = (append ? " >>" : " >") + " " + outputFile + " 2>> " + outputFile;
        bashStatements.add(stmt + redirectString);
        postCommandOutput(!bashStatements.isEmpty());
    }

    /**
     * Adds a command statement to the command output file for this Script.
     *
     * @param stmt  Command statement to be added
     * @param append  Whether or not the output file should be appended to
     * @see #addVerbatimStatement(String, String, boolean)
     */
    public void addVerbatimStatement(String stmt, boolean append) {
        addVerbatimStatement(stmt, CMD_OUTPUT_REF, append);
    }

    /**
     * Escapes control characters of a string for input to the printf command on Posix systems.
     *
     * @param s  printf input to escape
     * @return Escaped printf input
     */
    public static String printfEscape(String s) {
        return s.replaceAll("\\Q\\a\\E", Matcher.quoteReplacement("\\\\\\a"))
                .replaceAll("\\Q\\b\\E", Matcher.quoteReplacement("\\\\\\b"))
                .replaceAll("\\Q\\c\\E", Matcher.quoteReplacement("\\\\\\c"))
                .replaceAll("\\Q\\d\\E", Matcher.quoteReplacement("\\\\\\d"))
                .replaceAll("\\Q\\e\\E", Matcher.quoteReplacement("\\\\\\e"))
                .replaceAll("\\Q\\f\\E", Matcher.quoteReplacement("\\\\\\f"))
                .replaceAll("\\Q\\n\\E", Matcher.quoteReplacement("\\\\\\n"))
                .replaceAll("\\Q\\r\\E", Matcher.quoteReplacement("\\\\\\r"))
                .replaceAll("\\Q\\t\\E", Matcher.quoteReplacement("\\\\\\t"))
                .replaceAll("\\Q\\v\\E", Matcher.quoteReplacement("\\\\\\v"))
                .replaceAll("\\Q%\\E", Matcher.quoteReplacement("%%"))
                .replaceAll("\\Q\\'\\E", Matcher.quoteReplacement("\\\\'"))
                .replaceAll("\\Q\\\"\\E", Matcher.quoteReplacement("\\\\\""));
    }

    /**
     * Adds a printf statement redirected to the script output file. This is used to record what was
     * executed and when it started.
     *
     * Note for future work: All appending of redirect output should probably be refactored to be
     * managed by this class, rather than relying on code in the script manager.
     *
     * @param whatStarted  Command that was executed
     * @param append  Indicates whether or not to append the output file
     */
    private void preCommandOutput(String whatStarted, boolean append) {
        String escapedWhatStarted = printfEscape(whatStarted);
        StringBuilder s = new StringBuilder();
        String outToken;
        outToken = append ? ">>" : ">";
        s.append("printf \"command: ").append(escapedWhatStarted.replaceAll("\"", ""))
                .append("\\ntime started: `date +%s`\\n\" ")
                .append(outToken)
                .append(" ").append(SCRIPT_STATUS_REF)
                .append(" 2").append(outToken)
                .append(" ").append(SCRIPT_STATUS_REF);
        bashStatements.add(s.toString());
    }

    /**
     * Adds a printf statement redirected to the script output file. This is used to record the exit
     * status of the last command executed and the time execution ended.
     *
     * Note for future work: All appending of redirect output should probably be refactored to be
     * managed by this class, rather than relying on code in the script manager.
     *
     * @param append  Indicates whether or not to append the output file
     */
    private void postCommandOutput(boolean append) {
        StringBuilder s = new StringBuilder();
        String outToken;
        outToken = append ? ">>" : ">";
        s.append("printf \"exit status: $?\\ntime completed: `date +%s`\\n\" ")
                .append(outToken)
                .append(" ").append(SCRIPT_STATUS_REF)
                .append(" 2").append(outToken)
                .append(" ").append(SCRIPT_STATUS_REF);
        bashStatements.add(s.toString());
    }

    /**
     * Specifies the directory where redirected console output is saved by default. This includes
     * both the scriptStatus and cmdOutput files. This does not apply to statements created using
     * addVerbatimStatement where an outputFile is specified.
     *
     * @param directory  The directory where redirected console output is saved by default
     */
    public void setScriptOutputDirectory(String directory) {
        scriptOutputDirectory = ScriptManager.replaceTildeWithHome(directory);
    }

    /**
     * Provides the directory where redirected console output is saved by default.
     *
     * @return  The directory where redirected console output is saved by default
     */
    public String getScriptOutputDirectory() {
        return scriptOutputDirectory;
    }

    /**
     * Specifies the name of the default file used to redirect the output from commands executed by
     * the script. This file can be overridden for specific statements using the appropriate
     * functions with null filename strings (see printf(...) and addVerbatimStatement(...)), rather
     * than the generic functions.
     *
     * @param filename  The name of the default file used to redirect the output from commands
     *                  executed by the script
     */
    public void setCmdOutputFilename(String filename) {
        cmdOutputFilename = filename;
    }

    /**
     * Provides the filename of the file associated with logging script execution information.
     *
     * @return The filename of the file associated with logging script execution information
     */
    public String getCmdOutputFilename() {
        return cmdOutputFilename;
    }

    /**
     * Specifies the filename of the file associated with logging script execution information.
     *
     * @param filename  The filename of the file associated with logging script execution
     *                  information
     */
    public void setScriptStatusOutputFilename(String filename) {
        scriptStatusOutputFilename = filename;
    }

    /**
     * Provides the filename of the file associated with logging script execution information.
     *
     * @return The filename of the file associated with logging script execution information
     */
    public String getScriptStatusOutputFilename() {
        return scriptStatusOutputFilename;
    }

    /**
     * Constructs both the bash and batch scripts.
     *
     * @return True if the script was constructed successfully, otherwise false
     */
    public boolean construct() {
        bashScriptConstructed = constructBashScript();
        return bashScriptConstructed;
    }

    /**
     * Constructs the bash script from the previously constructed parts (see model data
     * declarations).
     *
     * @return True if the script was constructed successfully, otherwise false
     */
    private boolean constructBashScript() {
        StringBuilder builder = new StringBuilder();
        builder.append("#!/bin/bash").append('\n');
        builder.append("# script created on: ").append((new Date()).toString()).
                append("\n\n");
        // specify command line arguments as variables
        for (String bashArgDeclaration : bashArgDeclarations) {
            builder.append(bashArgDeclaration).append('\n');
        }
        // check arguments given
        builder.append('\n')
                // begin if
                .append("if [ \"$#\" -ne ")
                .append(bashArgDeclarations.size())
                .append(" ]; then\n")
                .append("\techo \"wrong number of arguments. expected ")
                .append(bashArgDeclarations.size()).append("\"\n");
        // provide usage
        builder.append("\techo \"usage: ").append(" ${0##*/}");
        for (String bashArgName : bashArgNames) {
            builder.append(" <").append(bashArgName).append('>');
        }
        builder.append("\"\n");
        for (int i = 0, im = usageStatements.size(); i < im; i++) {
            builder.append("\techo \"").append(i + 1).append('.').append('<')
                    .append(bashArgNames.get(i)).append('>').append(':')
                    .append(usageStatements.get(i)).append("\"\n");
        }
        // exit
        builder.append("exit 1\n")
                // end if
                .append("fi\n").append("\n");
        // assign script variables
        builder.append(SCRIPT_STATUS_VAR).append("=\"").append(scriptOutputDirectory).append("/")
                .append(scriptStatusOutputFilename).append("\"\n");
        builder.append(CMD_OUTPUT_VAR).append("=\"").append(scriptOutputDirectory).append("/")
                .append(cmdOutputFilename).append("\"\n");
        // run the programs with the given arguments
        for (String bashStatement : bashStatements) {
            builder.append(bashStatement).append('\n');
        }
        bashScript = builder.toString();
        // set flag for persistence
        bashScriptConstructed = true; //TODO: Is there ever a case when this should be false?
        return true;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Persistence">
    /**
     * Writes the script to disk. The script must be constructed prior to being persisted.
     *
     * @param scriptFilename  The relative, absolute, or canonical path to the file that will
     *                        contain the script data
     * @return True if both the bash script and batch script were persisted successfully, otherwise
     *         false
     */
    public boolean persist(String scriptFilename) {
        boolean bashScriptPersisted = false;
        if (bashScriptConstructed) {
            bashScriptPersisted = persistBashScript(scriptFilename);
        }
        return bashScriptPersisted;
    }

    /**
     * Writes the script to disk as a bash script. The script must be constructed by calling the
     * construct function prior to being persisted.
     *
     * @param scriptFilename  The relative, absolute, or canonical path to the file that will
     *                        contain the script data
     * @return True if the bash script was persisted successfully, otherwise false
     */
    public boolean persistBashScript(String scriptFilename) {
        boolean success = true;
        File scriptFile = new File(scriptFilename);
        try (FileWriter scriptWriter = new FileWriter(scriptFile, false)) {
            scriptWriter.write(bashScript);
        } catch (IOException e) {
            success = false;
        }
        return success;
    }

    /**
     * Provides the last name of a script persisted with a given version number.
     *
     * @param version  The version of the script persisted
     * @return The last name of the script without any directories
     */
    public static String getFilename(int version) {
        return "run_v" + version + ".sh";
    }

    /**
     * Returns the bash script as a string.
     *
     * @return The bash script
     */
    public String getBashScript() {
        return this.bashScript;
    }

    /**
     * Returns the list of bash statements for this Script.
     *
     * @return The list of bash statements for this Script
     */
    public List<String> getBashStatements() {
        return this.bashStatements;
    }
    // </editor-fold>
}
