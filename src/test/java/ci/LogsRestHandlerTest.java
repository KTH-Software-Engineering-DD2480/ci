package ci;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

public class LogsRestHandlerTest {
    /**
     * Makes sure that a log entry is converted to the desired JSON string.
     */
    @Test
    void logEntryToJsonWorks() {
        String repo =  "https://github.com/KTH-Software-Engineering-DD2480/ci";
        String refspec = "refs/heads/master";
        String commit = "this_is_a_test_commit";
        Date date = new Date(1644490638442L); // Just a (not quite) random date converted from milliseconds
        String output = "this is a test gradle output";
        Log_entry entry = new Log_entry(Helpers.Log_type.PUSH, repo, refspec, commit, date, Helpers.Status.success, output);

        JSONObject actual = LogsRestHandler.logEntryToRestJson(entry);

        String expected_string = """
            {
                "repo": "https://github.com/KTH-Software-Engineering-DD2480/ci",
                "refspec": "refs/heads/master",
                "commit": "this_is_a_test_commit",
                "datetime": {
                    "year": 2022,
                    "month": 1,
                    "day": 10,
                    "hour": 11,
                    "minute": 57,
                    "second": 18
                },
                "steps": [
                    {
                        "title": "Gradle Test",
                        "output": "this is a test gradle output"
                    }
                ]
            }
        """;

        JSONObject expected = new JSONObject(expected_string);
        
        assertEquals(expected.toString(), actual.toString());
    }
}
