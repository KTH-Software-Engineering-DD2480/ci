package ci;

import java.util.Date;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistentLogsTest {
    @AfterEach
    void delete_test_logs_directory() {
        File test_directory = new File("test_logs");
        if (test_directory.exists()) {
            File[] files = test_directory.listFiles();
            for (File file : files) {
                file.delete();
            }
            test_directory.delete();
        }
    }
    @Test
    void test_add_log_and_check_state() throws IOException {
        // Create a new directory for the test logs, deleting any existing test directory if it exists.
        File test_directory = new File("test_logs");
        if (!test_directory.mkdir()) {
            test_directory.delete();
            test_directory.mkdir();
        }
        PersistentLogs test_logs = new PersistentLogs("test_logs");
        Log_entry le = new Log_entry(Helpers.Log_type.PUSH, "https://github.com/KTH-Software-Engineering-DD2480/ci", "refs/heads/master",
            "this_is_a_test_commit", new Date(), Helpers.Status.success, "this is a test gradle output");
        int num_logs = test_logs.build_number;
        test_logs.add_log(le);
        assertEquals(num_logs + 1, test_logs.build_number);
        assertEquals(true, le.toString().equals(PersistentLogs.get_log(test_logs.all_logs()[num_logs]).toString()));
        test_logs.delete_logs();
        test_directory.delete();
    }
}
