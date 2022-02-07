package ci;

import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

public class Clone {
    
    public static boolean gitClone(String repo_url, String local_dir){
        try{
            // Clone the repository into the local_directory
            Git git = Git.cloneRepository().setURI(repo_url).setDirectory(local_dir).call();
            return true;
        } catch (Exception e){
            return false;
        }
        
    }
}

