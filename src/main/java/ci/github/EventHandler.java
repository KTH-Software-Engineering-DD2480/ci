package ci.github;

import java.io.IOException;

import org.json.JSONObject;

import jakarta.servlet.ServletException;

public interface EventHandler {
    void handle(JSONObject body) throws IOException, ServletException;
}
