package ci;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import java.io.File;

/** 
 * Utilities for managing a Git repository.
 */
public class Repository {
    /**
     * This function clones the repo and then checkout a specific commit.
     * @param repo_url The URL of the repository.
     * @param local_dir The path to a directory where the repository should be cloned to.
     * @param commit_id The SHA of the target commit.
     * @throws GitAPIException if clone failed
     * @throws JGitInternalException if {@code local_dir} already exists and is non-empty.
     */
    public static void gitClone(String repo_url, String local_dir, String commit_id) throws GitAPIException, JGitInternalException{
        // Clone the repository into the local_directory
        Git git = Git.cloneRepository().setURI(repo_url).setNoCheckout(true).setDirectory(new File(local_dir)).call();
        // checkout commit with hash commit_id, detached HEAD state
        git.checkout().setName(commit_id).call();
    }
}

