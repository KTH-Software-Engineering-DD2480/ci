package ci;

import java.util.Date;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistentLogsTest {
    @Test
    void test_add_log_and_check_state() {
        PersistentLogs logs = new PersistentLogs("logs");
        Log_entry le = new Log_entry(Log_entry.Log_type.TEST, "refs/heads/master", "this_is_a_test_commit", new Date(), Log_entry.Test_status.SUCCESS);
        int num_logs = logs.all_files().length;
        logs.add_log(le);
        assertEquals(num_logs + 1, logs.all_files().length);
        assertEquals(true, le.toString().equals(PersistentLogs.get_log(logs.all_files()[num_logs]).toString()));
        logs.delete_test_logs();
    }
}
