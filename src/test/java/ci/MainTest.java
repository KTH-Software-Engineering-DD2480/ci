package ci;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {
    @Test
    void alwaysTrue() {
        assertEquals(3, 2 + 1);
    }

    @Test
    void wrongAssertion() {
        assertEquals(4, 2 + 1);
    }
}

