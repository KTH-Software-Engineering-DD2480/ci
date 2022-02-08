package ci.github;

import java.io.IOException;
import java.util.HashMap;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import ci.RequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handles incoming GitHub webhooks.
 */
public class WebhookHandler extends AbstractHandler {
    HashMap<String, EventHandler> eventHandlers;

    /**
     * Creates a handler for the default set of GitHub webhook events.
     */
    public WebhookHandler() {
        // setup handlers for GitHub webhook events
        eventHandlers = new HashMap<>();
        eventHandlers.put("push", new PushEventHandler());
    }

    /**
     * Overriden from {@code AbstractHandler}. Handle an incoming GitHub webhook
     * request. The type of the event is extracted from the `X-GitHub-Event`
     * header (not the {@code target}).
     * @param target not used
     * @param baseRequest the incoming request from GitHub
     * @param request not used
     * @param response if the response was successful, fills with an {@code 200 OK} response
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // TODO: before handling the request we should verify that the `X-Hub-Signature-256` header has the expected value.
        // See here for details: https://docs.github.com/en/developers/webhooks-and-events/webhooks/webhook-events-and-payloads#delivery-headers

        String event = baseRequest.getHeader("X-GitHub-Event");
        if (event == null) {
            throw new RequestException(HttpServletResponse.SC_BAD_REQUEST, "missing `X-GitHub-Event` header");
        }

        System.out.println("handling event: " + event);

        EventHandler eventHandler = this.eventHandlers.get(event);
        if (eventHandler == null) {
            throw new RequestException(HttpServletResponse.SC_NOT_IMPLEMENTED, "the event `%s` is not supported".formatted(event));
        }

        try {
            JSONTokener tokens = new JSONTokener(baseRequest.getReader());
            JSONObject body = new JSONObject(tokens);
            eventHandler.handle(body);
        } catch (JSONException e) {
            throw new RequestException(HttpServletResponse.SC_BAD_REQUEST, e);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }
}

