package ci;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.Test;

public class CiTest {
    // Makes sure that we can succesfully execute a command
    @Test
    void assertCanExecuteCommand() throws IOException, InterruptedException {
        StringWriter output = new StringWriter();
        assertTrue(Ci.execCommand(new String[]{"echo", "hello"}, ".", output));
        assertEquals("hello\n", output.toString());
    }
}
