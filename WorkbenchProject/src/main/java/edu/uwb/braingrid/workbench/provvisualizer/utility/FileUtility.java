package edu.uwb.braingrid.workbench.provvisualizer.utility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbench.provvisualizer.model.Node;

public final class FileUtility {

    public static final String PREFIX_LOCAL = "local:";
    public static final String FILE_PATH_PREFIX_REGEX = "^(.*/)";
    public static final String FILE_PROTOCOL_REGEX = "^(.*://)";
    public static final String ARTIFACTS_DIR = "artifacts";

    private FileUtility() {
        // utility class cannot be instantiated
    }

    public static String getNodeFileRemoteRelativePath(Node node) {
        Path relPath = getNodeFileRelativePath(node);
        relPath = relPath.subpath(1, relPath.getNameCount());

        if (relPath.startsWith("~")) {
            relPath = relPath.subpath(1, relPath.getNameCount());
        }

        return FilenameUtils.separatorsToUnix(relPath.toString());
    }

    public static String getNodeFileLocalAbsolutePath(Node node) {
        return FileManager.getDefaultProjectDirectory().resolve(ARTIFACTS_DIR)
                .resolve(getNodeFileRelativePath(node)).toString();
    }

    private static Path getNodeFileRelativePath(Node node) {
        String nodeId = node.getId();

        if (nodeId.contains(PREFIX_LOCAL)) {
            return Paths.get(nodeId.replaceFirst(PREFIX_LOCAL, ""));
        } else {
            return Paths.get(nodeId.replaceFirst(FILE_PROTOCOL_REGEX, ""));
        }
    }

    public static List<String> fileToLines(String filename) {
        List<String> lines = new LinkedList<>();
        String line = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignore ... any errors should already have been
                    // reported via an IOException from the final flush.
                }
            }
        }
        return lines;
    }
}
