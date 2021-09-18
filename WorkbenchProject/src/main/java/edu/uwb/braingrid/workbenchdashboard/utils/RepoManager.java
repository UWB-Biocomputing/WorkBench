package edu.uwb.braingrid.workbenchdashboard.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;

import edu.uwb.braingrid.workbench.FileManager;
import edu.uwb.braingrid.workbenchdashboard.threads.RunUpdateRepo;

public final class RepoManager {

    private static final Logger LOG = Logger.getLogger(RepoManager.class.getName());

    /** Remote repository location. */
    public static final String BG_REPOSITORY_URI
            = "git://github.com/UWB-Biocomputing/BrainGrid.git";
    /** Name of the master branch. */
    public static final String MASTER_BRANCH_NAME = "master";
    private static boolean updatingBranch = false;

    private RepoManager() {
        // utility class cannot be instantiated
    }

    public static void updateMaster() throws GitAPIException, IOException {
        LOG.info("Updating Master Branch");
        updatingBranch = true;
        Git git;
        Path masterBranchPath = RepoManager.getMasterBranchDirectory();
        if (Files.exists(masterBranchPath)) {
            LOG.info("Updating Repo");
            git = Git.open(masterBranchPath.toFile());
            git.pull().call();
        } else {
            LOG.info("Cloning Repo");
            git = Git.cloneRepository()
                    .setURI(BG_REPOSITORY_URI)
                    .setDirectory(masterBranchPath.toFile())
                    .call();
        }
        updatingBranch = false;
    }

    public static boolean isUpdating() {
        return updatingBranch;
    }

    public static void getMasterBranch() {
        RunUpdateRepo updateRepo = new RunUpdateRepo();
        updateRepo.start();
    }

    public static Path getMasterBranchDirectory() {
        return FileManager.getBrainGridRepoDirectory().resolve(MASTER_BRANCH_NAME);
    }

    public static List<String> fetchGitBranches() {
        Collection<Ref> refs;
        List<String> branches = new ArrayList<>();
        try {
            refs = Git.lsRemoteRepository()
                    .setHeads(true)
                    .setRemote(BG_REPOSITORY_URI)
                    .call();
            for (Ref ref : refs) {
                branches.add(ref.getName().substring(ref.getName().lastIndexOf("/") + 1));
            }
            Collections.sort(branches);
        } catch (InvalidRemoteException e) {
            LOG.severe(" InvalidRemoteException occurred in fetchGitBranches\n" + e.getMessage());
            e.printStackTrace();
        } catch (TransportException e) {
            LOG.severe(" TransportException occurred in fetchGitBranches\n" + e.getMessage());
        } catch (GitAPIException e) {
            LOG.severe(" GitAPIException occur in fetchGitBranches\n" + e.getMessage());
        }
        return branches;
    }
}
