package edu.uwb.braingrid.workbench.comm;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import edu.uwb.braingrid.workbench.WorkbenchManager;
import edu.uwb.braingrid.workbench.ui.SimulationRuntimeDialog;
import java.io.InputStreamReader;
import javafx.scene.control.TextArea;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import riotcmd.infer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Provides abbreviated SSF/FTP functionality. This includes uploading/downloading files and
 * execution of commands on a remote machine.
 *
 * Created by Nathan on 7/27/2014.
 */
public class SecureFileTransfer {

    // <editor-fold defaultstate="collapsed" desc="Members">
    private static final int PORT = 22;
    private static final int BUFFER_SIZE = 1024;
    private Session session;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Construction">
    /**
     * Responsible for initialization of members and construction of SecureFileTransfer objects.
     */
    public SecureFileTransfer() {
        session = null;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Remote Operations">
    /**
     * Tests for the availability of an SFTP connection with a remote host.
     *
     * @param timeOut  The amount of time, in milliseconds, to wait for the connection to be
     *                 established. Setting this value less than zero will result in connect being
     *                 called without a timeout
     * @param hostname  The name of the host machine to connect to
     * @param username  The user's login username
     * @param password  The user's login password
     * @return True if the connection succeeded, otherwise false
     */
    public boolean testConnection(int timeOut, String hostname, String username, char[] password) {
        boolean success = true;
        try {
            JSch jsch = new JSch();

            // apply user info to connection attempt
            session = jsch.getSession(username, hostname, PORT);
            session.setPassword(String.valueOf(password));
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            if (timeOut > 0) {
                session.connect(timeOut);
            } else {
                session.connect();
            }
            success = true;
        } catch (JSchException e) {
            success = false;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return success;
    }

    // example taken from: http://kodehelp.com/java-program-for-uploading-file-to-sftp-server/
    /**
     * Uploads a file to a specified directory on a remote machine.
     *
     * @param fileToUpload  The file to be uploaded
     * @param remoteDirectory  The folder on the remote machine where the file should be uploaded.
     *                         This is a path relative to the root connection directory for the
     *                         user. On Linux systems this is the user's home folder (a.k.a ~/).
     * @param hostname  The name of the host machine to connect to
     * @param username  The user's login username
     * @param password  The user's login password
     * @param progressMonitor  An optional handler for progress reporting on the upload operation
     * @return True if the upload succeeded, otherwise false
     */
    public boolean uploadFile(String fileToUpload, String remoteDirectory, String hostname,
            String username, char[] password, SftpProgressMonitor progressMonitor)
            throws JSchException, FileNotFoundException, SftpException {
        boolean success = true;
        String altRemoteDirectory = remoteDirectory.startsWith("~/") ? remoteDirectory.substring(2)
                : remoteDirectory;
        try {
            JSch jsch = new JSch();
            // apply user info to connection attempt
            session = jsch.getSession(username, hostname, PORT);
            session.setPassword(String.valueOf(password));
            // generic setting up
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            // upload file
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            if (fileToUpload != null) {
                File f = new File(fileToUpload);
                if (f.exists()) {
                    String remotePath = altRemoteDirectory + "/" + f.getName();
                    if (progressMonitor != null) {
                        channelSftp.put(new FileInputStream(f), remotePath, progressMonitor,
                                ChannelSftp.OVERWRITE);
                    } else {
                        channelSftp.put(new FileInputStream(f), remotePath, ChannelSftp.OVERWRITE);
                    }
                } else {
                    success = false;
                }
            } else {
                success = false;
            }
            channelSftp.disconnect();
        } catch (JSchException | FileNotFoundException | SftpException e) {
            success = false;
            throw e;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return success;
    }

    /**
     * Downloads a specified file from the remote machine.
     *
     * @param remoteFilePath  The full path to the file on the remote machine. This is a path
     *                        (including the file name) relative to the root connection directory
     *                        for the user.
     * @param localFilePath  The location on the local machine where the file should be written to
     * @param hostname  The name of the host machine to connect to
     * @param username  The user's login username
     * @param password  The user's login password
     * @return True if the file was downloaded successfully, otherwise false
     */
    public boolean downloadFile(String remoteFilePath, String localFilePath, String hostname,
            String username, char[] password) throws JSchException, SftpException {
        boolean success = true;
        String altRemoteFilePath = remoteFilePath.startsWith("~/") ? remoteFilePath.substring(2)
                : remoteFilePath;
        try {
            JSch jsch = new JSch();
            // apply user info to connection attempt
            session = jsch.getSession(username, hostname, PORT);
            session.setPassword(String.valueOf(password));
            // generic setting up
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            // download file
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.get(altRemoteFilePath, localFilePath);
            channelSftp.disconnect();
        } catch (JSchException | SftpException e) {
            success = false;
            throw e;
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return success;
    }

    /**
     * Executes a command on the remote machine.
     *
     * @param command  The command to execute on the remote machine
     * @param hostname  The name of the host machine to connect to
     * @param username  The user's login username
     * @param password  The user's login password
     * @param readInputStream  Determines whether the result of the execution should be read. If the
     *                         command is executed through nohup, it is possible to disconnect and
     *                         leave the command running in the background. If this is set to true,
     *                         the command will be executed synchronously from the viewpoint of the
     *                         local machine, regardless of whether it was executed through nohup.
     * @return True if the command was executed successfully, otherwise false
     */
    public boolean executeCommand(String command, String hostname, String username, char[] password,
            boolean readInputStream) {
        boolean success = false;
        String altCommand = command.replaceAll("(?<=[^;\" ])~/", "");
        try {
            JSch jsch = new JSch();
            // apply user info to connection attempt
            session = jsch.getSession(username, hostname, PORT);
            session.setPassword(String.valueOf(password));
            // generic setting up
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            // setup command
            ChannelExec cExec = (ChannelExec) session.openChannel("exec");
            cExec.setCommand(altCommand);
            // setup I/O
            if (readInputStream) {
                cExec.setInputStream(null);
                cExec.setOutputStream(System.out);
                cExec.setErrStream(System.err);

                InputStream in = cExec.getInputStream();
                cExec.connect();
                success = readInputStream(in, cExec);
            } else {
                cExec.connect();
                success = true;
            }
        } catch (JSchException | IOException e) {
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
                System.out.println("disconnected from server @ " + (new Date()));
            }
        }
        return success;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Helper Functions">
    // helper function for the execute command only
    private boolean readInputStream(InputStream in, ChannelExec cExec) {
        boolean success = false;
        try {
            byte[] incomingBytes = new byte[BUFFER_SIZE];
            while (true) {
                // bytes are available to read from stream
                while (in.available() > 0) {
                    int i = in.read(incomingBytes, 0, BUFFER_SIZE);
                    // no bytes were read from the input stream, so stop
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(incomingBytes, 0, i));
                }
                // channel is closed
                if (cExec.isClosed()) {
                    // no more bytes left to read
                    if (in.available() == 0) {
                        // no bytes available, get exit status from command run
                        System.out.println("exit-status: "
                                + cExec.getExitStatus());
                        // determine whether the command was successful or not
                        success = cExec.getExitStatus() == 0;
                        break;
                    }
                }
                try { // separate attempts by one second
                    Thread.sleep(1000); //@cs-: MagicNumber influence 0
                } catch (InterruptedException ex) {
                }
            }
        } catch (IOException e) {
        }
        return success;
    }
    // </editor-fold>

  /**
   * Connects to the last simulation.
   *
   * @param hostname  The name of the host machine to connect to.
   * @param username  The user's login username.
   * @param password  The user's login password.
   * @param simName  The  last simulation to connect to.
   * @param manager  The temp place holder.
   */

  public void checkLastSim(String hostname, String username,
      String password, String simName, WorkbenchManager manager) {
    JSch jsch = new JSch();
    try {
      session = jsch.getSession(username, hostname, PORT);
      session.setPassword(password);
      session.setConfig("StrictHostKeyChecking", "no"); //optional
      session.connect();

      Channel channel = session.openChannel("exec");
      ((ChannelExec) channel).setInputStream(null);
      ((ChannelExec) channel).setCommand("cd WorkbenchSimulations/ && ls");
      channel.connect();

      InputStream in;
      try {
        in = channel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String files;
        while ((files = reader.readLine()) != null) {
          if (files.equals(simName)) {
            Channel channel2 = session.openChannel("sftp");
            channel2.connect();
            //((ChannelSftp) channel2).setInputStream(null);
            try {
              ((ChannelSftp) channel2).cd(
                  "WorkbenchSimulations//" + simName + "//Output//Debug");
              InputStream workBenchLog = ((ChannelSftp) channel2).get("workbench.txt");
              BufferedReader readLog = new BufferedReader(new InputStreamReader(workBenchLog));
              String line;
              String lastline = "";
              while ((line = readLog.readLine()) != null) {
                lastline = line;
              }
              String completion = "Complete";
              lastline = lastline.substring(
                  lastline.length() - completion.length(), lastline.length());
              if (lastline.equals("Complete")) {
                displayDownloadFrame(channel2, manager);
                break;
              }
            } catch (SftpException e) {
              e.printStackTrace();
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

    } catch (JSchException e) {
      e.printStackTrace();
    }
  }

  private void displayDownloadFrame(Channel channel, WorkbenchManager manager) {
    JFrame frame = new JFrame();
    String[] options = {"Download", "Cancel"};
    int option = JOptionPane.showOptionDialog(frame,
        "Last simlation completed, do you want to download?", "Last Simulation Completed",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
    if (option == JOptionPane.YES_NO_OPTION) {
      SimulationRuntimeDialog srd = new SimulationRuntimeDialog(
          new TextArea(manager.getMessages()));
    }
  }
}
