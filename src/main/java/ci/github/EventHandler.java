package ci.github;

import java.io.IOException;

import org.json.JSONObject;

import jakarta.servlet.ServletException;

/// The GitHub webhooks api defines multiple events that can be triggered. 
/// Classes implementing this interface should be able to handle one such event.
public interface EventHandler {
    /**
     * Handles a GitHub webhook event
     * @param body contents of the body of the webhook request
     * @throws IOException
     * @throws ServletException
     */
    void handle(JSONObject body) throws IOException, ServletException;
}
