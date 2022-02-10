package ci;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ViewLogsHandlerTest {
    /**
     * Makes sure that strings are properly escaped for safe use in HTML
     */
    @Test
    void escapeHtml() {
        String raw = "<script>alert(\"evil & dangerous\")</script>";
        String expected = "&lt;script&gt;alert(&quot;evil &amp; dangerous&quot;)&lt;/script&gt;";
        assertEquals(expected, ViewLogsHandler.escapeHtml(raw));
    }
}
