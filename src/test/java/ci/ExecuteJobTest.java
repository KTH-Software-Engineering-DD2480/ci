package ci;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;


public class ExecuteJobTest {
    static String testLogDirectory = "temp_logs";

    @AfterAll
    static void deleteTempLogDirectory() throws IOException {
        FileUtils.deleteDirectory(new File(testLogDirectory));
    }

    @Test
    public void executeTest() {
        ExecuteJob.execute(
            "https://github.com/KTH-Software-Engineering-DD2480/ci",
            "executeTestTemp",
            "26ff15a3a0465231fe512f05fd48fef06375e132",
            "https://api.github.com/repos/KTH-Software-Engineering-DD2480/ci/statuses/26ff15a3a0465231fe512f05fd48fef06375e132",
            "refs/heads/master",
            testLogDirectory);
        assertEquals(true, new File(testLogDirectory + "/0_PUSH.log").exists());
    }

}