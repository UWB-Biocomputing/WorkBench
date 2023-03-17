package edu.uwb.braingrid.workbench.comm;

import com.jcraft.jsch.Session;

 /**
  * This is the wrapper class for SecureFileTransfer that
  * will do the multi-threading to simultaneously keep track of
  * multiple simulations.
  *
  */
  public class SecureFileWrapperMultiThread implements Runnable {
    private String hostname;
    private String username;
    private String password;
    private String simName;
    private String msg;
    private SecureFileTransfer fileTransfer;

   /**
    * Connects to the last simulation.
    *
    * @param hostname  The name of the host machine to connect to.
    * @param username  The user's login username.
    * @param password  The user's login password.
    * @param simName  The  last simulation to connect to.
    * @param msg  The message to display
    */
    public SecureFileWrapperMultiThread(String hostname, String username,
        String password, String simName, String msg) {
        fileTransfer = new SecureFileTransfer();
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.simName = simName;
        this.msg = msg;
    }
     /**
      * Starts to keep track of the progress.
      *
      */
      public void run() {
          fileTransfer.checkLastSim(hostname, username, password, simName, msg);
      }
}
