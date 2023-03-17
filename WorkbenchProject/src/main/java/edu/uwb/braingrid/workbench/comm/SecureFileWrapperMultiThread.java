package edu.uwb.braingrid.workbench.comm;

import com.jcraft.jsch.Session;

public class SecureFileWrapperMultiThread implements Runnable{
	
	private String hostname;
	private String username;
	private String password;
	private String simName;
	private String msg;
	
	private SecureFileTransfer fileTransfer;
	public SecureFileWrapperMultiThread(String hostname, String username,
		      String password, String simName, String msg) {
		fileTransfer = new SecureFileTransfer();
		this.hostname = hostname;
		this.username =username;
		this.password = password;
		this.simName = simName;
		this.msg = msg;
	}

	  public void run() {  
		  fileTransfer.checkLastSim(hostname, username, password, simName, msg);
	  }
	  
}
