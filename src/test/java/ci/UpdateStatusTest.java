package ci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UpdateStatusTest {
    @Test
    public void updateStatusTestValidInput() {
        String SHA = "23fe422e554fd622540f84c5b69e691f0ffd6c83";
        String status = "failure";

        UpdateStatus.updateStatus(SHA, status);
        assertEquals(UpdateStatus.getStatus(SHA), status);
    }

    @Test
    public void updateStatusTestInvalidInput() {
        String SHA = "23fe422e554fd622540f84c5b69e691f0ffd6c83";
        String status = "fail";

        UpdateStatus.updateStatus(SHA, status);
        assertNotEquals(UpdateStatus.getStatus(SHA), status);
    }
}

