package ci.github;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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

/**
 * Handles incoming GitHub webhooks.
 */
public class WebhookHandler extends AbstractHandler {
    HashMap<String, EventHandler> eventHandlers;
    // stores the secret used by the GitHub webhook (conceptually this is just a password)
    private String ciGitHubSecret;

    /**
     * Creates a handler for the default set of GitHub webhook events.
     */
    public WebhookHandler() {
        ciGitHubSecret = getGitHubSecretFromEnvironment();

        // setup handlers for GitHub webhook events
        eventHandlers = new HashMap<>();
        eventHandlers.put("push", new PushEventHandler());
    }
  
    // load the GitHub secret token from an environemnt variable
    private static String getGitHubSecretFromEnvironment() {
        String secret = System.getenv("CI_GITHUB_SECRET");
        if (secret == null) {
            throw new RuntimeException("CI_GITHUB_SECRET environment variable not set (must be set to the same value as the one used on GitHub)");
        }
        return secret;
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

    // Computes a SHA256 HMAC signature of the given contents using the secret used by GitHub webhooks.
    // See here for details: https://docs.github.com/en/developers/webhooks-and-events/webhooks/securing-your-webhooks#validating-payloads-from-github
    String bodySignature(String body) throws RequestException {
        try {
            return "sha256=" + signature(ciGitHubSecret, body);
        } catch (Exception e) {
            throw new RequestException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        }
    }

    // Computes a SHA256 HMAC signature of the given contents.
    public static String signature(String secret, String content) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(secret.getBytes("UTF-8"), algorithm));

        byte[] hash = mac.doFinal(content.getBytes("UTF-8"));

        return bytesToHex(hash);
    }

    // Converts a sequence of bytes into a hex-digit representation
    static String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(2*bytes.length);
        for (byte b : bytes) {
            builder.append("%02x".formatted(b));
        }
        return builder.toString();
    }
}

