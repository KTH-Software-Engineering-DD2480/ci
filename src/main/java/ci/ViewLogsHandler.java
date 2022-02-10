package ci;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewLogsHandler extends AbstractHandler {
    PersistentLogs logs;

    public ViewLogsHandler() {
        logs = new PersistentLogs("logs");
    }

    /**
     * Overriden from {@code AbstractHandler}. 
     * 
     * Handle an incoming REST API request: returns a number of log files as a HTML page.
     * 
     * @param target not used
     * @param baseRequest the incoming request
     * @param request not used
     * @param response if the response was successful, fills with an {@code 200 OK} response
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        var parameters = baseRequest.getParameterMap();

        // default values for the parameters
        int offset = 0;
        int count = 10;

        // parse overriden values of the parameters
        for (var entry : parameters.entrySet()) {
            String name = entry.getKey();
            try {
                for (String value : entry.getValue()) {
                    if (value.length() == 0) continue;

                    if (name.equals("offset")) {
                        offset = Integer.parseInt(value);
                    } else if (name.equals("count")) {
                        count = Integer.parseInt(value);
                    } else {
                        throw RequestException.badRequest("invalid parameter `%s`".formatted(name));
                    }
                }
            } catch (NumberFormatException e) {
                throw RequestException.badRequest("failed to parse `%s`: %s".formatted(name, e.getMessage()));
            }
        }

        if (offset < 0) {
            throw RequestException.badRequest("expected a parameter `offset` in the range `0..`");
        }

        if (count < 1 || count > 25) {
            throw RequestException.badRequest("expected a parameter `count` in the range `1..25`");
        }

        Log_entry[] log_entries = this.logs.getLogRange(offset, count);
        int logCount = this.logs.all_logs().length;

        String html = buildHtmlResponse(log_entries, offset, count, logCount);

        Writer writer = response.getWriter();
        writer.write(html);
        writer.flush();
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }

    /**
     * Generates a HTML page that displays the given log entries
     * @param entries the log entries to display
     * @param offset the {@code offset} used to view the current logs 
     * @param count the {@code count} used to view the current logs 
     * @param logCount the number of logs in the history
     * @return the HTML page
     */
    static String buildHtmlResponse(Log_entry[] entries, int offset, int count, int logCount) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("""
            <head>
                <title>CI-17</title>
            </head>
        """);
        builder.append("<body>");
        buildNavigationHtml(builder, offset, count, logCount);

        for (int i = 0; i < entries.length; i++) {
            buildLogEntryHtml(builder, entries[i]);
        }

        buildNavigationHtml(builder, offset, count, logCount);
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }

    /**
     * Generate a HTML snippet for navigation buttons
     * @param builder where to write the snippet
     * @param offset the {@code offset} used to view the current logs 
     * @param count the {@code count} used to view the current logs 
     * @param logCount the number of logs in the history
     */
    static void buildNavigationHtml(StringBuilder builder, int offset, int count, int logCount) {
        int previousOffset = Math.max(0, offset - count);
        int nextOffset = offset + count;

        String previous = "/view-logs?offset=" + previousOffset + "&count=" + count;
        String next = "/view-logs?offset=" + nextOffset + "&count=" + count;

        builder.append("<table>");
        builder.append("<tr>");
        if (offset > 0) {
            builder.append("<td><a href=\"" + escapeHtml(previous) + "\">previous</a></td>");
        }
        if (nextOffset < logCount) {
            builder.append("<td><a href=\"" + escapeHtml(next) + "\">next</a></td>");
        }
        builder.append("</tr>");
        builder.append("</table>");
    }

    /**
     * Generate a HTML snippet for the given log entry
     * @param builder where to write the snippet
     * @param entry the log entry to display
     */
    static void buildLogEntryHtml(StringBuilder builder, Log_entry entry) {
        builder.append("<div>");

        String title = "%s @ %s".formatted(entry.date_time.toString(), entry.refspec);
        builder.append("<h3>" + escapeHtml(title) + "</h3>");

        builder.append("<table>");
        builder.append("<tr><th>Status</th><td>" + escapeHtml(entry.status.toString()) + "</td></tr>");
        String url = escapeHtml(entry.repo_url);
        builder.append("<tr><th>Repository</th><td><a href=\"" + url + "\">" + url + "</a></td></tr>");
        builder.append("<tr><th>Commit</th><td>" + escapeHtml(entry.commit_SHA) + "</td></tr>");
        builder.append("</table>");

        builder.append("<div style=\"border:1px solid gray;\">");
        builder.append("<pre style=\"margin:1em;\">" + escapeHtml(entry.gradle_output) + "</pre>");
        builder.append("</div>");

        builder.append("</div>");
    }

    /**
     * Replaces any potentially dangerous characters with their HTML entity counterparts. 
     * Important to not accidentally perform an XSS.
     * @return the escaped HTML string
     */
    static String escapeHtml(String text) {
        byte[] raw = text.getBytes(StandardCharsets.UTF_8);
        var buffer = new ByteArrayOutputStream(raw.length);

        for (byte b : raw) {
            byte[] replacement = HTML_ESCAPES_UTF_8[b];
            if (replacement == null) {
                buffer.write(b);
            } else {
                buffer.writeBytes(replacement);
            }
        }

        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * An array of 256 byte-strings. 
     * At index {@code i} is the HTML entity corresponding to the ascii character {@code i}.
     */
    static byte[][] HTML_ESCAPES_UTF_8 = new byte[256][];
    static {
        HTML_ESCAPES_UTF_8['&'] = "&amp;".getBytes(StandardCharsets.UTF_8);
        HTML_ESCAPES_UTF_8['<'] = "&lt;".getBytes(StandardCharsets.UTF_8);
        HTML_ESCAPES_UTF_8['>'] = "&gt;".getBytes(StandardCharsets.UTF_8);
        HTML_ESCAPES_UTF_8['"'] = "&quot;".getBytes(StandardCharsets.UTF_8);
        HTML_ESCAPES_UTF_8['\''] = "&#39;".getBytes(StandardCharsets.UTF_8);
    }
}
