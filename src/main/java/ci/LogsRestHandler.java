package ci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogsRestHandler extends AbstractHandler {
    PersistentLogs logs;

    public LogsRestHandler() {
        logs = new PersistentLogs("logs");
    }

    /**
     * Overriden from {@code AbstractHandler}. Handle an incoming REST API request: returns a number of log files
     * @param target not used
     * @param baseRequest the incoming request
     * @param request not used
     * @param response if the response was successful, fills with an {@code 200 OK} response
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        var parameters = baseRequest.getParameterMap();

        int offset = 0;
        int count = 0;

        for (var entry : parameters.entrySet()) {
            try {
                String name = entry.getKey();
                for (String value : entry.getValue()) {
                    if (name.equals("offset")) {
                        offset = Integer.parseInt(value);
                    } else if (name.equals("count")) {
                        count = Integer.parseInt(value);
                    } else {
                        throw RequestException.badRequest("invalid parameter `%s`".formatted(name));
                    }
                }
            } catch (NumberFormatException e) {
                throw RequestException.badRequest(e);
            }
        }

        if (offset < 0) {
            throw RequestException.badRequest("expected a parameter `offset` in the range `0..`");
        }

        if (count < 1 || count > 25) {
            throw RequestException.badRequest("expected a parameter `count` in the range `1..25`");
        }

        Log_entry[] log_entries = this.logs.getLogRange(offset, count);

        JSONObject root = new JSONObject();

        JSONArray entries = new JSONArray();
        for (var log : log_entries) {
            entries.put(logEntryToRestJson(log));
        }
        root.put("entries", entries);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        root.write(writer);
        writer.flush();
        baseRequest.setHandled(true);
    }

    /**
     * Converts a log entry to the JSON format expected by the REST api.
     * @param log the log entry to convert
     * @return a JSON object representing the log entry.
     */
    static JSONObject logEntryToRestJson(Log_entry log) {
        JSONObject entry = new JSONObject();
        entry.put("repo", log.repo_url);
        entry.put("refspec", log.refspec);
        entry.put("commit", log.commit_SHA);
        entry.put("datetime", dateToRestJson(log.date_time));

        JSONArray steps = new JSONArray();

        JSONObject gradle_step = new JSONObject();
        gradle_step.put("title", "Gradle Test");
        gradle_step.put("output", log.gradle_output);
        steps.put(gradle_step);

        entry.put("steps", steps);

        return entry;
    }

    /**
     * Convert a {@link java.util.Date} to a JSON object with fields for years, month, days, etc.
     * @param date the date to convert
     * @return a JSON object representing the date
     */
    static JSONObject dateToRestJson(Date date) {
        var calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Stockholm"), Locale.forLanguageTag("sv"));
        calendar.setTime(date);

        JSONObject time = new JSONObject();
        time.put("year", calendar.get(Calendar.YEAR));
        time.put("month", calendar.get(Calendar.MONTH));
        time.put("day", calendar.get(Calendar.DAY_OF_MONTH));
        time.put("hour", calendar.get(Calendar.HOUR));
        time.put("minute", calendar.get(Calendar.MINUTE));
        time.put("second", calendar.get(Calendar.SECOND));
        return time;
    }
}
