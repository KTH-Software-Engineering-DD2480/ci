package ci;

import java.io.File;
import org.apache.commons.io.FileUtils;
import java.io.StringWriter;
import java.util.Date;

import ci.Helpers.Status;
import ci.Helpers.Log_type;


public class ExecuteJob {

    public static void execute(String repoUrl, String localDir, String commitSha, String apiUrl, String refspec, String logsFolder) {
        Log_entry log = new Log_entry(Log_type.PUSH, repoUrl, refspec, commitSha, new Date(), Status.error, null);

        try {
            UpdateStatus.updateStatus(apiUrl, Status.pending);
        } catch(Exception e) {
            System.err.println(e.toString());
        }

        try {
            Repository.gitClone(repoUrl, localDir, commitSha);

            StringWriter outputTest = new StringWriter();
            boolean result = Ci.gradleTest(localDir, outputTest);

            log.gradle_output = outputTest.toString();
            log.status = result == true ? Status.success : Status.failure;
        } catch(Exception e1) {
            log.gradle_output = e1.toString();
            try {
                UpdateStatus.updateStatus(apiUrl, Status.error);
            } catch(Exception e2) {
                System.err.println(e2.toString());
            }
        } finally {
            try {
                FileUtils.deleteDirectory(new File(localDir));
            } catch (Exception e) {
                System.err.println("failed to delete directory: " + localDir);
            }
        }

        PersistentLogs persistentLogs = new PersistentLogs(logsFolder);

        try {
            persistentLogs.add_log(log);
            UpdateStatus.updateStatus(apiUrl, log.status);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }
}
