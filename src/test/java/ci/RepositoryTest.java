import org.junit.jupiter.api.Test;

import ci.Repository;

import java.io.IOException;
import java.beans.Transient;
import java.io.File;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.apache.commons.io.FileUtils;


public class RepositoryTest{

	// Define the repo url
	private static final String repo_url = "https://github.com/KTH-Software-Engineering-DD2480/Repo-clone-test.git";

	// assert false if we can not file the file
    @Test
    void assertFalseIfFileNotFound() throws GitAPIException, JGitInternalException, IOException{
		// Define the commit we want to check out
		String commit_id = "568d764486a15d462045032d7896636e4e8cf4a7";

		// Define the temporary folder name
		String path = "tempFolder1";

		// clone the repo
		Repository clone = new Repository();
		clone.gitClone(repo_url, path, commit_id);

		// check if the file exists (it should not)
		boolean found = new File(path + "/test1.txt").exists();
		assertFalse(found);
	
		// Delete the folder after testing
		FileUtils.deleteDirectory(new File(path));
	

    }

	// assert true if we can find the file
	@Test
	void assertTrueIfFileIsFound() throws GitAPIException, JGitInternalException, IOException{
		// Define the commit we want to check out
		String commit_id = "46774b9410eeb70d83101825d41d1fce84b910bd";
	
		// Define the temporary folder name
		String path = "tempFolder2";
	
		// clone the repo
		Repository clone = new Repository();
		clone.gitClone(repo_url, path, commit_id);

		// check if the file exists (it should)
		boolean found = new File(path + "/test1.txt").exists();
		assertTrue(found);
		
		// Delete the folder after testing
		FileUtils.deleteDirectory(new File(path));
	}
	

}

