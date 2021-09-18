package edu.uwb.braingrid.workbench.provvisualizer.utility;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import edu.uwb.braingrid.workbench.provvisualizer.model.AuthenticationInfo;

public final class ConnectionUtility {

    /** Port used for SSH file transfer. */
    public static final int SFTP_PORT = 22;

    private ConnectionUtility() {
        // utility class cannot be instantiated
    }

    public static boolean downloadFileViaSftp(String remoteFilePath, String localFilePath,
            AuthenticationInfo authenticationInfo) {
        return downloadFileViaSftp(remoteFilePath, localFilePath, authenticationInfo.getHostname(),
                authenticationInfo.getUsername(), authenticationInfo.getPassword().toCharArray());
    }

    public static boolean downloadFileViaSftp(String remoteFilePath, String localFilePath,
            String hostname, String username, char[] password) {
        boolean success = true;
        Session session = null;

        try {
            JSch jsch = new JSch();
            // apply user info to connection attempt
            session = jsch.getSession(username, hostname, SFTP_PORT);
            session.setPassword(String.valueOf(password));
            // generic setting up
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            // download file
            ChannelSftp channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            Path dir = Paths.get(localFilePath).getParent();
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            channelSftp.get(remoteFilePath, localFilePath);
            channelSftp.disconnect();
        } catch (JSchException | SftpException e) {
            success = false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
        return success;
    }
}
