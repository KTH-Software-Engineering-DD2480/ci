package ci;

import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import java.io.File;

public class Clone {
    
    // this function clones the repo and then checkout a specific commit
    public static boolean gitClone(String repo_url, String local_dir, String commit_id) throws GitAPIException{

            // Clone the repository into the local_directory
            Git git = Git.cloneRepository().setURI(repo_url).setNoCheckout(true).setDirectory(new File(local_dir)).call();
            // checkout commit with hash commit_id, detached HEAD state
            git.checkout().setName(commit_id).call();

            return true;
    }

}

