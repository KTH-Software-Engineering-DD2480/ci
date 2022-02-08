package ci.github;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.json.JSONException;
import org.json.JSONObject;

import ci.RequestException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebhookHandler extends AbstractHandler {
    HashMap<String, EventHandler> eventHandlers;

    public WebhookHandler() {
        // setup handlers for GitHub webhook events
        eventHandlers = new HashMap<>();
        eventHandlers.put("push", new PushEventHandler());
    }

    // Handle an incoming GitHub webhook request. The type of the event is extracted from the `X-GitHub-Event` header (not the `target`).
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String body = bodyContents(baseRequest);

        // Check that the signature computed by GitHub (using our secret) matches the value we expect.
        // This is used to verify that nobody else manufactured a request and sent it to our CI server.
        String signature = baseRequest.getHeader("X-Hub-Signature-256");
        String expectedSignature = bodySignature(body);
        if (signature == null || !signature.equals(expectedSignature)) {
            throw new RequestException(HttpServletResponse.SC_FORBIDDEN, "invalid `X-Hub-Signature-256`");
        }

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
            eventHandler.handle(new JSONObject(body));
        } catch (JSONException e) {
            throw new RequestException(HttpServletResponse.SC_BAD_REQUEST, e);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }

    /// Collects the contents of the request's body in a String
    static String bodyContents(Request request) throws IOException {
        StringWriter bodyWriter = new StringWriter(request.getContentLength());
        request.getReader().transferTo(bodyWriter);
        return bodyWriter.toString();
    }

    // stores the secret used by the GitHub webhook (conceptually this is just a password)
    private static String CI_GITHUB_SECRET;

    // load the CI_GITHUB_SECRET from an environemnt variable with the same name
    static {
        String secret = System.getenv("CI_GITHUB_SECRET");
        if (secret == null) {
            throw new RuntimeException("CI_GITHUB_SECRET environment variable not set (must be set to the same value as the one used on GitHub)");
        }
        CI_GITHUB_SECRET = secret;
    }

    // Computes a SHA256 HMAC signature of the given contents.
    // See here for details: https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks#validating-payloads-from-github
    static String bodySignature(String body) throws RequestException {
        try {
            String algorithm = "HmacSHA256";
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(CI_GITHUB_SECRET.getBytes("UTF-8"), algorithm));

            byte[] hash = mac.doFinal(body.getBytes("UTF-8"));

            return "sha256=" + bytesToHex(hash);
        } catch (Exception e) {
            throw new RequestException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    // Converts a sequence of bytes into a hex-digit representation
    static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append("%02x".formatted(b));
        }
        return builder.toString();
    }
}

