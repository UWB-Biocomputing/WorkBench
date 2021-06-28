package edu.uwb.braingrid.workbench.comm;

import com.jcraft.jsch.SftpProgressMonitor;
import java.io.File;
import javax.swing.JProgressBar;

/**
 * Provides call back functions to monitor the progress of SecureFileTransfer operations.
 *
 * @author Del Davis
 */
public class ProgressBarProgressMonitor implements SftpProgressMonitor {

    private JProgressBar bar;
    private File file = null;
    private long max;

    /**
     * Responsible for initializing and constructing this monitor object.
     *
     * @param bar  The progress bar to update
     * @param file  The file being transferred
     */
    public ProgressBarProgressMonitor(JProgressBar bar, File file) {
        if (file != null && file.exists()) {
            this.file = file;
        }
        this.bar = bar;
    }

    /**
     * Initializes the progress bar and file transfer progress. Will be called when new operation
     * starts.
     *
     * @param op  A code indicating the direction of transfer, one of PUT and GET
     * @param src  The source file name
     * @param dest  The destination file name
     * @param length  The final count (i.e. length of file to transfer)
     */
    @Override
    public void init(int op, String src, String dest, long length) {
        bar.setValue(bar.getMinimum());
        if (max == -1 && file != null) {
            max = file.length();
        } else {
            max = length;
        }
    }

    /**
     * Updates the progress of the transfer. Will be called periodically as more data is
     * transferred.
     *
     * @param bytes  The number of bytes transferred so far
     * @return True if the transfer should go on, false if the transfer should be cancelled.
     */
    @Override
    public boolean count(long bytes) {
        long percentageComplete = 0;
        if (max > 0) {
            percentageComplete = (long) ((float) bytes / max * 100); //@cs-: MagicNumber influence 0
        }
        bar.setValue((int) percentageComplete);
        return true;
    }

    /**
     * Will be called when the transfer ended, either because all the data was transferred, or
     * because the transfer was cancelled.
     */
    @Override
    public void end() {
        bar.setValue(bar.getMaximum());
    }
}
