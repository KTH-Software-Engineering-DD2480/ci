package ci;

import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import java.io.File;

public class Repository {
    
    // this function clones the repo and then checkout a specific commit
    // Throws GitAPIException if clone failed
    // Throws JGitInternalException if path local_dir already exist
    public static void gitClone(String repo_url, String local_dir, String commit_id) throws GitAPIException, JGitInternalException{

            // Clone the repository into the local_directory
            Git git = Git.cloneRepository().setURI(repo_url).setNoCheckout(true).setDirectory(new File(local_dir)).call();
            // checkout commit with hash commit_id, detached HEAD state
            git.checkout().setName(commit_id).call();

    }

}

