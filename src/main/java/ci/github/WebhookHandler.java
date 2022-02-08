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

public class WebhookHandler extends AbstractHandler {
    HashMap<String, EventHandler> eventHandlers;

    public WebhookHandler() {
        eventHandlers = new HashMap<>();
        eventHandlers.put("push", new PushEventHandler());
    }

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

